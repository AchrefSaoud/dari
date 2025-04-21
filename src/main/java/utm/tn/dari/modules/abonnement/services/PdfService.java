package utm.tn.dari.modules.abonnement.services;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Abonnement;

import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public String generateAbonnementPdf(Abonnement abonnement) {
        String dest = "abonnement_" + abonnement.getId() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Détails de l'abonnement"));
            document.add(new Paragraph("Nom : " + abonnement.getNom()));
            document.add(new Paragraph("Description : " + abonnement.getDescription()));
            document.add(new Paragraph("Type : " + abonnement.getType()));
            document.add(new Paragraph("Prix : " + abonnement.getPrix() + " DT"));

            if (abonnement.getDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                document.add(new Paragraph("Date : " + abonnement.getDate().format(formatter)));
            }

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return dest;
    }
}
