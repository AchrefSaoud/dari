package utm.tn.dari.modules.annonce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailingServiceImpl  implements MailingService {


    @Autowired
    private JavaMailSender mailSender;

    public void sendNotificationEmailForNewAnnouncementToUser(String to, Long announcementId) {
        try {

            String subject = "Notification de Nouvelle Annonce";
            String body = String.format("""
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Notification de Nouvelle Annonce</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f4f4f4;
                                padding: 20px;
                            }
                            .container {
                                background-color: #fff;
                                padding: 20px;
                                border-radius: 5px;
                                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                            }
                            h1 {
                                color: #333;
                            }
                            p {
                                color: #555;
                            }
                            a {
                                color: #007BFF;
                                text-decoration: none;
                            }
                            a:hover {
                                text-decoration: underline;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>Nouvelle Annonce</h1>
                            <p>Bonjour,</p>
                            <p>Une nouvelle annonce correspondant à vos critères de recherche a été publiée.</p>
                            <p>Vous pouvez consulter l'annonce en cliquant sur le lien ci-dessous :</p>
                            <p><a href="http://localhost:4200/annonces/%d">Voir l'annonce</a></p>
                            <p>Merci d'utiliser notre service !</p>
                            <p>Cordialement,</p>
                            <p>Nom de votre entreprise</p>
                        </div>
                    </body>
                    </html>
                    """,announcementId);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailSender.createMimeMessage());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true); // true indicates that the text is HTML


            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email to user: " + to);
        }
    }

}