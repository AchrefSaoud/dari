package utm.tn.dari.modules.annonce.Utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class MultipartFileCompressor {

    public static String compressThenSaveMultipartFile(MultipartFile file, String outputDir , String prefix, float quality) throws IOException {
        // Validate file type
        if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only image files are supported.");
        }

        // Read the input image
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IOException("Failed to read the image file.");
        }

        // Get the file extension
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") && !fileExtension.equals("png")) {
            throw new IllegalArgumentException("Unsupported file format. Only jpg, jpeg, and png are supported.");
        }

        // Get an ImageWriter for the file format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(fileExtension);
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writer found for format: " + fileExtension);
        }
        ImageWriter writer = writers.next();

        // Set compression parameters
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); // Set quality (0.0 to 1.0)
        }

        Path uploadPath = Paths.get(outputDir);
        Files.createDirectories(uploadPath);


        String fileRelativePath = prefix +"_" + file.getOriginalFilename();
        String fileFinalPath =  uploadPath.resolve(fileRelativePath).toString();
        // Write the compressed image to the specified output file
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new File(fileFinalPath))) {
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
        return fileRelativePath;
    }

    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }
}