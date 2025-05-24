package utm.tn.dari.modules.chatBot.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VectorStoreService {
    void storeEmbedding(String documentId, String chunkId, List<Double> embedding, String text);
    List<Map.Entry<String, String>> findSimilarChunks(List<Double> queryEmbedding, int topK);
    void deleteDocument(String documentId);
    Set<String> getAllDocumentIds();
}