package utm.tn.dari.modules.annonce.services;

import java.util.List;

public interface MailingService {
    void sendNotificationEmailForNewAnnouncementToUser(String to, Long announcementId);
    void sendPriceChangeNotificationEmail(String to, Long announcementId, String title, float oldPrice, float newPrice);
    void sendNotificationEmailForNewAnnouncementToUsers(List<String> to, Long announcementId);
}
