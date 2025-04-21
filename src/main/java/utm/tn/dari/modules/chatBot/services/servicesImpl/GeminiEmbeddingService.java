package utm.tn.dari.modules.chatBot.services.servicesImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import utm.tn.dari.modules.chatBot.services.VectorEmbeddingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiEmbeddingService implements VectorEmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiEmbeddingService.class);
    private final WebClient webClient;
    private final String apiKey;
    private final String embeddingModel;
    private final ObjectMapper objectMapper;

    public GeminiEmbeddingService(WebClient geminiWebClient,
                                 @Value("${gemini.api.key}") String apiKey,
                                 @Value("${embedding.model}") String embeddingModel,
                                 ObjectMapper objectMapper) {
        this.webClient = geminiWebClient;
        this.apiKey = apiKey;
        this.embeddingModel = embeddingModel;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Double> generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.error("Single text input is null or empty");
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        return generateEmbeddings(List.of(text)).get(0);
    }

    @Override
    public List<List<Double>> generateEmbeddings(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            logger.error("Text list is null or empty");
            throw new IllegalArgumentException("Text list cannot be null or empty");
        }

        List<String> validTexts = texts.stream()
                .filter(text -> text != null && !text.trim().isEmpty())
                .collect(Collectors.toList());

        if (validTexts.isEmpty()) {
            logger.error("No valid texts after filtering");
            throw new IllegalArgumentException("No valid texts to embed");
        }

        logger.debug("Generating embeddings for {} texts", validTexts.size());
        validTexts.forEach(text -> logger.debug("Text: {}", text.substring(0, Math.min(100, text.length()))));

        Map<String, Object> requestBody = Map.of(
                "requests", validTexts.stream()
                        .map(text -> Map.of(
                                "model", "models/" + embeddingModel,
                                "content", Map.of(
                                        "parts", List.of(Map.of("text", text))
                                )
                        ))
                        .toList()
        );
        try {
            logger.debug("Embedding request body: {}", objectMapper.writeValueAsString(requestBody));
        } catch (Exception e) {
            logger.error("Error serializing request body", e);
        }

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:batchEmbedContents")
                        .queryParam("key", apiKey)
                        .build(embeddingModel))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> {
                                    logger.error("Embedding API error response: {}", error);
                                    return Mono.error(new RuntimeException("Embedding API error: " + error));
                                }))
                .bodyToMono(Map.class)
                .map(response -> {
                    Object embeddingsObject = response.get("embeddings");
                    if (!(embeddingsObject instanceof List)) {
                        logger.error("Expected embeddings to be a List, got: {}", embeddingsObject);
                        throw new IllegalStateException("Expected embeddings to be a List");
                    }
                    List<?> embeddingsList = (List<?>) embeddingsObject;
                    return embeddingsList.stream()
                            .map(embedding -> {
                                if (!(embedding instanceof Map)) {
                                    logger.error("Expected embedding to be a Map, got: {}", embedding);
                                    throw new IllegalStateException("Expected each embedding to be a Map");
                                }
                                @SuppressWarnings("unchecked")
                                Map<String, Object> embeddingMap = (Map<String, Object>) embedding;
                                Object valuesObj = embeddingMap.get("values");
                                if (!(valuesObj instanceof List)) {
                                    logger.error("Expected values to be a List, got: {}", valuesObj);
                                    throw new IllegalStateException("Expected values to be a List");
                                }
                                @SuppressWarnings("unchecked")
                                List<Double> values = (List<Double>) valuesObj;
                                return values;
                            })
                            .toList();
                })
                .block();
    }
}