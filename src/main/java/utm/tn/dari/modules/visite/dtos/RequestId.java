package utm.tn.dari.modules.visite.dtos;

import jakarta.validation.constraints.NotNull;

public class RequestId {
    @NotNull
    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
