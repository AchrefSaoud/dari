package utm.tn.dari.modules.chatBot.services;


import org.springframework.web.multipart.MultipartFile;
import utm.tn.dari.modules.chatBot.dtos.GeminiRequest;
import utm.tn.dari.modules.chatBot.dtos.GeminiResponse;
import java.io.IOException;

public interface GeminiService {
    GeminiResponse chatWithGemini(GeminiRequest chatRequest);
    String uploadFile(MultipartFile file) throws IOException;
}