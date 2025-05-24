package utm.tn.dari.modules.chatBot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.HttpClientErrorException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import utm.tn.dari.modules.chatBot.services.GeminiService;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import utm.tn.dari.modules.chatBot.dtos.GeminiRequest;
import utm.tn.dari.modules.chatBot.dtos.GeminiResponse;

@RestController
@RequestMapping("/api/chat")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<GeminiResponse> askQuestion(@RequestBody GeminiRequest.Part userMessage) {
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

        try {
            GeminiResponse response = geminiService.chatWithGemini(chatRequest);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String response = geminiService.uploadFile(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/documents")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Set<String>> getAllDocuments() {
        try {
            Set<String> documentIds = geminiService.getAllDocuments();
            return ResponseEntity.ok(documentIds);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/document/{documentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteDocument(@PathVariable String documentId) {
        try {
            String result = geminiService.deleteDocument(documentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting document: " + e.getMessage());
        }
    }
}