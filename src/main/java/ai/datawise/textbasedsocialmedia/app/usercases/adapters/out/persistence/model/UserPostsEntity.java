package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "user_posts", schema = "giannis")
public class UserPostsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "text", nullable = false, length = 3000)
    private String text;
    @Basic
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Integer userId;
    @Basic
    @Column(name = "post_date", nullable = false)
    Timestamp postDate;
    @Version
    private Integer version;


    @OneToMany(mappedBy = "userPostsEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCommentsEntity> postComments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    RegisteredUsersEntity registeredUsersEntity;

    public void addPostComment(PostCommentsEntity postCommentsEntity)
    {
        postComments.add(postCommentsEntity);
        postCommentsEntity.setUserPostsEntity(this);
    }
    public void removePostComment(PostCommentsEntity postCommentsEntity) {
        postComments.remove(postCommentsEntity);
        postCommentsEntity.setUserPostsEntity(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPostsEntity that = (UserPostsEntity) o;
        return id == that.id && Objects.equals(text, that.text)
                && Objects.equals(userId, that.userId)
                && Objects.equals(postDate, that.postDate)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, userId, postDate, version);
    }
}
