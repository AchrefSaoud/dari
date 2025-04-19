package utm.tn.dari.modules.chatBot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import utm.tn.dari.modules.chatBot.services.GeminiService;
import java.io.IOException;
import java.util.List;
import utm.tn.dari.modules.chatBot.dtos.GeminiRequest;
import utm.tn.dari.modules.chatBot.dtos.GeminiResponse;

@RestController
@RequestMapping("/api/chatbot")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public Mono<ResponseEntity<GeminiResponse>> askQuestion(@RequestBody GeminiRequest.Part userMessage) {
        GeminiRequest chatRequest = new GeminiRequest();
        chatRequest.setContents(List.of(
            new GeminiRequest.Content(
                List.of(
                    new GeminiRequest.Part(""),
                    new GeminiRequest.Part(userMessage.getText())
                ),
                "user"
            )
        ));
        chatRequest.setGenerationConfig(new GeminiRequest.GenerationConfig(
            2048,
            0.4,   
            0.9    
        ));

        return geminiService.chatWithGemini(chatRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class, e -> 
                    Mono.just(ResponseEntity.status(e.getStatusCode()).build()))
                .onErrorResume(e -> 
                    Mono.just(ResponseEntity.internalServerError().build()));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return geminiService.uploadFile(file)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> 
                        Mono.just(ResponseEntity.badRequest().body("Error: " + e.getMessage())));
        } catch (IOException e) {
            return Mono.just(ResponseEntity.badRequest().body("Error processing file: " + e.getMessage()));
        }
    }

    @DeleteMapping("/document/{documentId}")
    public Mono<ResponseEntity<String>> deleteDocument(@PathVariable String documentId) {
        return Mono.just(ResponseEntity.ok("Document deletion not implemented in this example"));
    }
}