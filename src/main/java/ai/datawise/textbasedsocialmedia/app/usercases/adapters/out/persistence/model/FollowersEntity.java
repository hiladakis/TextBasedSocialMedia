package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "followers", schema = "giannis")
public class FollowersEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "follower_user_id", nullable = false, insertable = false, updatable = false)
    private Integer followerUserId;
    @Basic
    @Column(name = "followed_user_id", nullable = false, insertable = false, updatable = false)
    private Integer followingUserId;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "followed_user_id")
    RegisteredUsersEntity followed;

    @ManyToOne
    @JoinColumn(name = "follower_user_id")
    RegisteredUsersEntity follower;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowersEntity that = (FollowersEntity) o;
        return id == that.id && Objects.equals(followerUserId, that.followerUserId)
                && Objects.equals(followingUserId, that.followingUserId)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, followerUserId, followingUserId, version);
    }
}
