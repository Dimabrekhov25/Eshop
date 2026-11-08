package accounts;

import lombok.Getter;
import lombok.Setter;
import accounts.enums.StaffRole;
import java.util.UUID;

@Getter
@Setter
public class AdminProfile {
    private final UUID userId;
    private String displayName;
    private StaffRole role;
    private boolean isActive = true;

    public AdminProfile(UUID userId, String displayName, StaffRole role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }
}

