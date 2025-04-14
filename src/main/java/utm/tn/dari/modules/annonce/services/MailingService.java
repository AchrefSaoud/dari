package utm.tn.dari.modules.annonce.services;

public interface MailingService {
    void sendNotificationEmailForNewAnnouncementToUser(String to, Long announcementId);

}
