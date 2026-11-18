package accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_user_profiles_user_id"))
    private User user;

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "phone", length = 40)
    @Setter
    private String phone;

    protected UserProfile() {
    }

    public UserProfile(User user, String name) {
        this.user = Objects.requireNonNull(user, "user");
        this.userId = user.getId();
        this.name = name;
    }

    public UserProfile(User user, String name, String phone) {
        this(user, name);
        this.phone = phone;
    }
}
