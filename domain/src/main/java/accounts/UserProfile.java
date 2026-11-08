package accounts;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserProfile {
    private final UUID userId;
    private String name;
    private String phone; // Optional.

    public UserProfile(UUID userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}

