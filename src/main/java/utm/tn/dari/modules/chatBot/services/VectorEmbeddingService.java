package utm.tn.dari.modules.chatBot.services;

import java.util.List;

public interface VectorEmbeddingService {
    List<Double> generateEmbedding(String text);
    List<List<Double>> generateEmbeddings(List<String> texts);
}