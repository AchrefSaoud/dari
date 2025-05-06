package utm.tn.dari.modules.visite.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

public class CreateSlotRequest {
    @NotNull
    private long userId;
    @NotNull
    private Long annonceId;

    @Future
    private LocalDateTime startTime;

    @Future
    private LocalDateTime endTime;

    public Long getAnnonceId() {
        return annonceId;
    }

    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
// Getters and setters
}
