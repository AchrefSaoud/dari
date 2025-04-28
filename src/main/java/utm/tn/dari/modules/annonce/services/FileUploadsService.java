package utm.tn.dari.modules.annonce.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class FileUploadsService {

    public ResponseEntity<Resource> serveResourceByPath(String filePath) throws IOException {
        Path uploadDir = Paths.get("dari/uploads"); // base folder
        Path file = uploadDir.resolve(filePath).normalize(); // safe resolve
        System.out.println(file.toString());
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read file: " + filePath);
        }
    }
}
