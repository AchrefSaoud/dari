package utm.tn.dari.modules.chatBot.services.servicesImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.chatBot.services.VectorStoreService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryVectorStore implements VectorStoreService {

    private final Map<String, Map<String, List<Double>>> documentChunks = new ConcurrentHashMap<>();
    private final Map<String, String> chunkTexts = new ConcurrentHashMap<>();
    private final int topK;

    public InMemoryVectorStore(@Value("${top.k.chunks}") int topK) {
        this.topK = topK;
    }

    @Override
    public void storeEmbedding(String documentId, String chunkId, List<Double> embedding, String text) {
        documentChunks.computeIfAbsent(documentId, k -> new ConcurrentHashMap<>())
                     .put(chunkId, embedding);
        chunkTexts.put(chunkId, text);
    }

    @Override
    public List<Map.Entry<String, String>> findSimilarChunks(List<Double> queryEmbedding, int topK) {
        return documentChunks.values().stream()
                .flatMap(chunks -> chunks.entrySet().stream())
                .map(chunkEntry -> {
                    double similarity = cosineSimilarity(queryEmbedding, chunkEntry.getValue());
                    return new AbstractMap.SimpleEntry<>(chunkEntry.getKey(), similarity);
                })
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(topK)
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), chunkTexts.get(entry.getKey())))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDocument(String documentId) {
        Map<String, List<Double>> chunks = documentChunks.remove(documentId);
        if (chunks != null) {
            chunks.keySet().forEach(chunkTexts::remove);
        }
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}