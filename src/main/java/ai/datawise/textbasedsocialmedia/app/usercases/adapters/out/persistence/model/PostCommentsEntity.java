package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;
@Data

@Entity
@Table(name = "post_comments", schema = "giannis")
public class PostCommentsEntity
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "comment", nullable = false, length = 3000)
    private String comment;
    @Basic
    @Column(name = "post_id", nullable = false, insertable = false, updatable = false)
    private Integer postId;
    @Basic
    @Column(name = "comment_user", nullable = false)
    private String commentUser;
    @Basic
    @Column(name = "comment_date", nullable = false)
    Timestamp commentDate;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "post_id")
    UserPostsEntity userPostsEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCommentsEntity that = (PostCommentsEntity) o;
        return id == that.id && Objects.equals(comment, that.comment)
                && Objects.equals(postId, that.postId)
                && Objects.equals(commentUser, that.commentUser)
                && Objects.equals(commentDate, that.commentDate)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comment, postId, commentDate, version);
    }
}
