package accounts;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseEntity {
    private String email;
    private String passwordHash;
    private boolean isActive = true;

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}

