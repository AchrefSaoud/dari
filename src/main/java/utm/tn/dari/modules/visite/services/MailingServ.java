package utm.tn.dari.modules.visite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Visite;

@Service
public class MailingServ {


    @Autowired
    private JavaMailSender mailSender;

    public void sendNotificationEmailForNewRDVToUser( Visite visite) {
        try {

            String subject = "Notification d'une visite'";
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
                            <h1>Nouvelle Visite a été réservée</h1>
                            <p>Bonjour,</p>
                            <p>Mr / Mme %s vient de confirmer</p>
                            <p>le RDV à partir de %s</p>
                            <p>au %s </p>
                            <p>pour l'annonce %s</p>
                            <p>Cordialement,</p>
                            <p>Dari</p>
                            <p>28483699</p>
                        </div>
                    </body>
                    </html>
                    """,visite.getClient().getNom(),visite.getStartTime(),visite.getEndTime(),visite.getAnnonce().getTitre());

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailSender.createMimeMessage());
            mimeMessageHelper.setTo(visite.getOwner().getUsername());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true); // true indicates that the text is HTML


            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email to user: " + visite.getOwner().getUsername());
        }
    }

    public void sendVisiteDeletedNotification(Visite visite) {
        try {

            String subject = "Annulation d'une visite'";
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
                            <h1>Visite a étè annulé par le propriétaire</h1>
                                         <p>%s</p>

                            <p>Cordialement,</p>
                            <p>Dari</p>
                            <p>28483699</p>
                        </div>
                    </body>
                    </html>
                    """,visite.getAnnonce().getTitre());

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailSender.createMimeMessage());
            mimeMessageHelper.setTo(visite.getOwner().getUsername());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true); // true indicates that the text is HTML


            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email to user: " + visite.getOwner().getUsername());
        }
    }
}