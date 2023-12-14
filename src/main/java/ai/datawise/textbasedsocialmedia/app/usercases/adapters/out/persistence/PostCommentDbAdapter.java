package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.PostCommentsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.PostCommentPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostCommentDbAdapter implements PostCommentPort
{
    private static final Logger logger = LogManager.getLogger(PostCommentDbAdapter.class);
    @Override
    public boolean isPremiumUser(int user_id)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        RegisteredUsersEntity registeredUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, user_id);

        if(registeredUsersEntity != null && registeredUsersEntity.getRole().equalsIgnoreCase("Premium"))
        {
            return true;
        }
        return false;
    }

    @Override
    public int getPostCommentsNumber(int post_id, int user_id)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        RegisteredUsersEntity registeredUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, user_id);

        UserPostsEntity userPostsEntity = entityManager
                .find(UserPostsEntity.class, post_id);

        List<PostCommentsEntity> postCommentsEntityList = userPostsEntity.getPostComments();
        Predicate<PostCommentsEntity> usernamePredicate = postComment -> postComment
                .getCommentUser().equals(registeredUsersEntity.getUsername());

        return postCommentsEntityList.stream().filter(usernamePredicate).toList().size();
    }

    @Override
    public PostCommentResponse storePostComment(PostComment postComment)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        RegisteredUsersEntity registeredUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, postComment.getUserId());

        UserPostsEntity userPostsEntity = entityManager
                .find(UserPostsEntity.class, postComment.getPostId());

        PostCommentsEntity postCommentsEntity = getPostCommentsEntityFromPostComment(postComment,
                registeredUsersEntity.getUsername());

        userPostsEntity.addPostComment(postCommentsEntity);
        entityManager.persist(userPostsEntity);

        logger.info("postCommentsEntity id : "+postCommentsEntity.getId());
        return new PostCommentResponse(postCommentsEntity.getId(),postCommentsEntity.getCommentDate());
    }

    private PostCommentsEntity getPostCommentsEntityFromPostComment(PostComment postComment, String username)
    {
        PostCommentsEntity postCommentsEntity = new PostCommentsEntity();
        postCommentsEntity.setPostId(postComment.getPostId());
        postCommentsEntity.setComment(postComment.getComment());
        postCommentsEntity.setCommentDate(new Timestamp(System.currentTimeMillis()));
        postCommentsEntity.setCommentUser(username);
        return postCommentsEntity;
    }

}
