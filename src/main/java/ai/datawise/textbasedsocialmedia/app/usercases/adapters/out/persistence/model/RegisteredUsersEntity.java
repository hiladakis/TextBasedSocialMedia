package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "registered_users", schema = "giannis")
public class RegisteredUsersEntity
{
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "username", nullable = false, length = 255)
    private String username;
    @Basic
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Basic
    @Column(name = "role", nullable = false, length = 7)
    private String role;

    @OneToMany(mappedBy = "registeredUsersEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPostsEntity> userPosts = new ArrayList<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowersEntity> followersEntities = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowersEntity> followingEntities = new ArrayList<>();

    @Version
    private int version;

    public void addFollower(FollowersEntity followersEntity)
    {
        followersEntities.add(followersEntity);
        followersEntity.setFollowed(this);
    }
    public void removeFollower(FollowersEntity followersEntity)
    {
        followersEntities.remove(followersEntity);
        followersEntity.setFollowed(null);
    }

    public void addFollowing(FollowersEntity followingEntity)
    {
        followingEntities.add(followingEntity);
        followingEntity.setFollower(this);
    }
    public void removeFollowing(FollowersEntity followingEntity)
    {
        followingEntities.remove(followingEntity);
        followingEntity.setFollower(null);
    }

    public void addPost(UserPostsEntity userPostsEntity)
    {
        userPosts.add(userPostsEntity);
        userPostsEntity.setRegisteredUsersEntity(this);
    }
    public void removePost(UserPostsEntity userPostsEntity) {
        userPosts.remove(userPostsEntity);
        userPostsEntity.setRegisteredUsersEntity(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredUsersEntity that = (RegisteredUsersEntity) o;
        return id == that.id && Objects.equals(username, that.username)
                && Objects.equals(password, that.password)
                && Objects.equals(role, that.role)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role, version);
    }
}
