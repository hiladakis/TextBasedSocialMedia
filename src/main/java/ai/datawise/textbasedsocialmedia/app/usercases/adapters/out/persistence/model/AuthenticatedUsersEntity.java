package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Data
@Table(name = "authenticated_users", schema = "giannis")
public class AuthenticatedUsersEntity
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "username", nullable = false, length = 255)
    private String username;
    @Basic
    @Column(name = "role", nullable = false, length = 7)
    private String role;
    @Basic
    @Column(name = "login_date", nullable = false)
    Timestamp loginDate;

    @Version
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatedUsersEntity that = (AuthenticatedUsersEntity) o;
        return id == that.id && Objects.equals(username, that.username)
                && Objects.equals(role, that.role)
                && Objects.equals(loginDate, that.loginDate)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, role, loginDate, version);
    }
}
