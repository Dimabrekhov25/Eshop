package accounts;

import accounts.enums.StaffRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "admin_profiles")
public class AdminProfile {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_admin_profiles_user_id"))
    private User user;

    @Column(name = "display_name", nullable = false, length = 200)
    @Setter
    private String displayName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "role", nullable = false, length = 32)
    @Setter
    private StaffRole role;

    @Column(name = "is_active", nullable = false)
    @Setter
    private boolean isActive = true;

    protected AdminProfile() {
    }

    public AdminProfile(User user, String displayName, StaffRole role) {
        this.user = Objects.requireNonNull(user, "user");
        this.userId = user.getId();
        this.displayName = displayName;
        this.role = role;
    }
}
