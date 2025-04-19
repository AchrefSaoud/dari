package utm.tn.dari.modules.chatBot.services.servicesImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.chatBot.services.DocumentProcessingService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

    @Value("${chunk.size}")
    private int chunkSize;
    
    @Value("${chunk.overlap}")
    private int chunkOverlap;
    
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("(?<=[.!?])\\s+");

    @Override
    public String extractTextFromFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        String fileName = path.getFileName().toString().toLowerCase();
        
        if (fileName.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(new File(filePath))) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else if (fileName.endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(filePath);
                 XWPFDocument document = new XWPFDocument(fis);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                return extractor.getText();
            }
        } else if (fileName.endsWith(".txt")) {
            return Files.readString(path);
        } else {
            throw new UnsupportedOperationException("Unsupported file type: " + fileName);
        }
    }

    @Override
    public List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        String[] sentences = SENTENCE_PATTERN.split(text.trim());
        
        StringBuilder currentChunk = new StringBuilder();
        int currentLength = 0;
        
        for (String sentence : sentences) {
            if (currentLength + sentence.length() > chunkSize && currentLength > 0) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder();
                currentLength = 0;
                
                if (chunkOverlap > 0 && !chunks.isEmpty()) {
                    String lastChunk = chunks.get(chunks.size() - 1);
                    int overlapStart = Math.max(0, lastChunk.length() - chunkOverlap);
                    String overlapText = lastChunk.substring(overlapStart);
                    currentChunk.append(overlapText).append(" ");
                    currentLength = overlapText.length() + 1;
                }
            }
            
            currentChunk.append(sentence).append(" ");
            currentLength += sentence.length() + 1;
        }
        
        if (currentLength > 0) {
            chunks.add(currentChunk.toString());
        }
        
        return chunks;
    }

    @Override
    public void indexDocumentChunks(String documentId, List<String> chunks) {
        
    }
}