package utm.tn.dari.modules.annonce.services;

public interface MailingService {
    void sendNotificationEmailForNewAnnouncementToUser(String to, Long announcementId);
    void sendPriceChangeNotificationEmail(String to, Long announcementId, String title, float oldPrice, float newPrice);
}
