package utm.tn.dari.modules.chatBot.services;

import java.io.IOException;
import java.util.List;

public interface DocumentProcessingService {
    String extractTextFromFile(String filePath) throws IOException;
    List<String> chunkText(String text, int chunkSize);
    void indexDocumentChunks(String documentId, List<String> chunks);
}