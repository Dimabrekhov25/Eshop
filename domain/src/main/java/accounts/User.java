package accounts;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uq_users_email", columnNames = {"email"})
})
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, length = 320)
    @Setter
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    @Setter
    private String passwordHash;

    @Column(name = "is_active", nullable = false)
    @Setter
    private boolean isActive = true;

    protected User() {
    }

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
