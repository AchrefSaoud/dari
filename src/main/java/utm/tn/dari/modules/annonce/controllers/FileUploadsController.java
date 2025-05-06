package utm.tn.dari.modules.annonce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import utm.tn.dari.modules.annonce.services.FileUploadsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller()
@RequestMapping("api/files")
public class FileUploadsController {

    @Autowired
    private FileUploadsService fileUploadsService;
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("{filePath:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filePath) {
        try {

            System.out.println(filePath);
            int splits = filePath.split("/").length;
            if(splits > 1){
                filePath = filePath.split("/")[splits - 1];
            }
          return fileUploadsService.serveResourceByPath(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
