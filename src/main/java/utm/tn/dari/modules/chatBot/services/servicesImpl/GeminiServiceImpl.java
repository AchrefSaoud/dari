package utm.tn.dari.modules.chatBot.services.servicesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import utm.tn.dari.modules.chatBot.dtos.GeminiRequest;
import utm.tn.dari.modules.chatBot.dtos.GeminiResponse;
import utm.tn.dari.modules.chatBot.services.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiServiceImpl.class);
    
    private final WebClient webClient;
    private final String apiKey;
    private final StorageService storageService;
    private final DocumentProcessingService documentProcessingService;
    private final VectorEmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final int topKChunks;

    public GeminiServiceImpl(WebClient geminiWebClient,
                           @Value("${gemini.api.key}") String apiKey,
                           StorageService storageService,
                           DocumentProcessingService documentProcessingService,
                           VectorEmbeddingService embeddingService,
                           VectorStoreService vectorStoreService,
                           @Value("${top.k.chunks}") int topKChunks) {
        this.webClient = geminiWebClient;
        this.apiKey = apiKey;
        this.storageService = storageService;
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
        this.topKChunks = topKChunks;
    }

    @Override
    public Mono<GeminiResponse> chatWithGemini(GeminiRequest chatRequest) {
        try {
            // Extract user question
            String userQuestion = chatRequest.getContents().get(0).getParts().get(1).getText();
            
            // Generate embedding for the question
            List<Double> questionEmbedding = embeddingService.generateEmbedding(userQuestion);
            
            // Retrieve relevant chunks
            List<Map.Entry<String, String>> relevantChunks = 
                vectorStoreService.findSimilarChunks(questionEmbedding, topKChunks);
            
            // Build context from relevant chunks
            String context = relevantChunks.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.joining("\n\n---\n\n"));
            
            // Augment the prompt with context
            String systemMessage = buildSystemMessage(context);
            String augmentedPrompt = buildAugmentedPrompt(userQuestion, context);
            
            // Update the request
            chatRequest.getContents().get(0).getParts().get(0).setText(systemMessage);
            chatRequest.getContents().get(0).getParts().get(1).setText(augmentedPrompt);
            
            logger.debug("Sending request to Gemini with context: {}", context);
            
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-1.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(chatRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                               .flatMap(error -> {
                                   logger.error("Gemini API error: {}", error);
                                   return Mono.error(new RuntimeException("Gemini API error: " + error));
                               }))
                    .bodyToMono(GeminiResponse.class);
        } catch (Exception e) {
            logger.error("Error in chatWithGemini", e);
            return Mono.error(e);
        }
    }

    private String buildSystemMessage(String context) {
        return """
            You are Dari, a smart, reliable assistant designed to help users by answering their questions clearly and accurately.
            You must always base your answers strictly on the provided context. 
            If the context doesn’t contain the necessary information, be honest and say: “Sorry, I don’t have enough information to answer that.”
            Avoid guessing or generating information that is not present in the context.
            
            Here is the relevant context to help answer the user's question:
            
            """ + context;
    }
    

    private String buildAugmentedPrompt(String question, String context) {
        return """
            Based on the following context, provide a clear and concise answer to the question.
            Make sure your response is helpful, factual, and grounded in the information given.
            
            Question: %s
            
            Context: 
            %s
            
            Answer:
            """.formatted(question, context);
    }
    

    @Override
    public Mono<String> uploadFile(MultipartFile file) throws IOException {
        try {
            String documentId = UUID.randomUUID().toString();
            String filePath = storageService.store(file);
            logger.info("Stored file {} with document ID {}", file.getOriginalFilename(), documentId);
    
            String text = documentProcessingService.extractTextFromFile(filePath);
            if (text == null || text.trim().isEmpty()) {
                logger.error("Extracted text is empty or null for file {}", file.getOriginalFilename());
                throw new IllegalStateException("Cannot process empty text from file");
            }
            logger.debug("Extracted text (length: {}): {}", text.length(), text.substring(0, Math.min(100, text.length())));
    
            List<String> chunks = documentProcessingService.chunkText(text, 1000);
            logger.info("Created {} chunks for document {}", chunks.size(), documentId);
            if (chunks.isEmpty()) {
                logger.error("No chunks created for document {}", documentId);
                throw new IllegalStateException("No valid chunks created from text");
            }
    
            chunks.forEach(chunk -> logger.debug("Chunk content: {}", chunk.substring(0, Math.min(100, chunk.length()))));
    
            List<List<Double>> embeddings = embeddingService.generateEmbeddings(chunks);
    
            for (int i = 0; i < chunks.size(); i++) {
                String chunkId = documentId + "_" + i;
                vectorStoreService.storeEmbedding(documentId, chunkId, embeddings.get(i), chunks.get(i));
            }
    
            return Mono.just("Document processed successfully. ID: " + documentId);
        } catch (Exception e) {
            logger.error("Error processing file {}", file.getOriginalFilename(), e);
            throw e;
        }
    }
}