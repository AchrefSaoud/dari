package utm.tn.dari.modules.chatBot.services;


import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import utm.tn.dari.modules.chatBot.dtos.GeminiRequest;
import utm.tn.dari.modules.chatBot.dtos.GeminiResponse;
import java.io.IOException;

public interface GeminiService {
    Mono<GeminiResponse> chatWithGemini(GeminiRequest chatRequest);
    Mono<String> uploadFile(MultipartFile file) throws IOException;
}