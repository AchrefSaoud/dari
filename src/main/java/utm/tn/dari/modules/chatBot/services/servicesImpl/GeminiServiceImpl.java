package utm.tn.dari.modules.chatBot.services.servicesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
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
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final StorageService storageService;
    private final DocumentProcessingService documentProcessingService;
    private final VectorEmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final int topKChunks;
    private final String geminiBaseUrl;

    public GeminiServiceImpl(RestTemplate restTemplate,
                           @Value("${gemini.api.key}") String apiKey,
                           @Value("${gemini.api.url}") String geminiBaseUrl,
                           StorageService storageService,
                           DocumentProcessingService documentProcessingService,
                           VectorEmbeddingService embeddingService,
                           VectorStoreService vectorStoreService,
                           @Value("${top.k.chunks}") int topKChunks) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.geminiBaseUrl = geminiBaseUrl;
        this.storageService = storageService;
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
        this.topKChunks = topKChunks;
    }

    @Override
    public GeminiResponse chatWithGemini(GeminiRequest chatRequest) {
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
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Build the request
            HttpEntity<GeminiRequest> requestEntity = new HttpEntity<>(chatRequest, headers);
            
            // Build the URL
            String url = geminiBaseUrl + "/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            
            // Make the request
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                GeminiResponse.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Gemini API error: {}", response.getStatusCode());
                throw new RuntimeException("Gemini API error: " + response.getStatusCode());
            }
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error in chatWithGemini", e);
            throw new RuntimeException("Error communicating with Gemini API", e);
        }
    }

    private String buildSystemMessage(String context) {
        return """
            You are Dari, an intelligent, friendly, and helpful AI assistant designed to provide excellent support while maintaining a warm, professional tone.
            
            Personality Traits:
            - Friendly and approachable
            - Professional yet conversational
            - Empathetic and understanding
            - Helpful and resourceful
            - Positive and encouraging
            
            Communication Guidelines:
            1. For greetings and casual conversation:
               - Respond naturally to greetings ("Hello!", "Hi there!")
               - Answer common small talk appropriately ("I'm doing well, thanks for asking! How about you?")
               - Keep casual responses brief but friendly
            
            2. For information requests:
               - First try to answer using the provided context below
               - If context exists but is incomplete, say "Based on what I know..." and answer partially
               - If no relevant context exists, you may use your general knowledge but indicate it's not from provided sources
               - If completely unsure, say "I'm not entirely sure about that, but I can try to help you find the answer."
            
            3. Always maintain:
               - Clear, concise language
               - Proper grammar and punctuation
               - A helpful, positive attitude
            
            Relevant Context (use for factual answers):
            """ + context;
    }
    
    private String buildAugmentedPrompt(String question, String context) {
        return """
            User Question: %s
            
            Available Context:
            %s
            
            Instructions:
            1. Analyze if the question is:
               a) A greeting/small talk - respond appropriately
               b) A factual question - answer using context if available
               c) A complex question - break it down and answer parts you can
            
            2. If context exists but doesn't fully answer:
               - Acknowledge what you can answer
               - Note any limitations
               - Offer to help find more information if needed
            
            3. Response should be:
               - Natural and conversational
               - Helpful and accurate
               - Appropriately detailed
               - Friendly but professional
            
            Please provide your best response:
            """.formatted(question, context);
    }
    
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
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
    
            return "Document processed successfully. ID: " + documentId;
        } catch (Exception e) {
            logger.error("Error processing file {}", file.getOriginalFilename(), e);
            throw e;
        }
    }
}