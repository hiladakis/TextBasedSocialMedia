package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.PostCommentsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.PostCommentPort;
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
    @Setter
    @Getter
    private EntityManagerFactory entityManagerFactory = null;
    private static final Logger logger = LogManager.getLogger(PostCommentDbAdapter.class);
    @Override
    public boolean isPremiumUser(int user_id) throws Exception
    {
        EntityManager entityManager = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();

            RegisteredUsersEntity registeredUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, user_id);

            if(registeredUsersEntity != null && registeredUsersEntity.getRole().equalsIgnoreCase("Premium"))
            {
                entityManager.close();
                return true;
            }
        }
        catch (Exception  t)
        {
            if (entityManager != null) {
                entityManager.close();
            }
            throw t;
        }
        return false;
    }

    @Override
    public int getPostCommentsNumber(int post_id, int user_id) throws Exception
    {
        EntityManager entityManager = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();

            RegisteredUsersEntity registeredUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, user_id);

            UserPostsEntity userPostsEntity = entityManager
                    .find(UserPostsEntity.class, post_id);

            List<PostCommentsEntity> postCommentsEntityList = userPostsEntity.getPostComments();
            Predicate<PostCommentsEntity> usernamePredicate = postComment -> postComment
                    .getCommentUser().equals(registeredUsersEntity.getUsername());

            int postCommentsNumber = postCommentsEntityList.stream().filter(usernamePredicate).toList().size();
            entityManager.close();

            return postCommentsNumber;
        }
        catch (Exception  t)
        {
            if (entityManager != null) {
                entityManager.close();
            }
            throw t;
        }
    }

    @Override
    public PostCommentResponse storePostComment(PostComment postComment) throws Exception
    {
        EntityManager entityManager = null;
        EntityTransaction trans = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();
            trans = entityManager.getTransaction();
            trans.begin();

            RegisteredUsersEntity registeredUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, postComment.getUserId());

            UserPostsEntity userPostsEntity = entityManager
                    .find(UserPostsEntity.class, postComment.getPostId());

            PostCommentsEntity postCommentsEntity = getPostCommentsEntityFromPostComment(postComment,
                    registeredUsersEntity.getUsername());

            userPostsEntity.addPostComment(postCommentsEntity);
            entityManager.persist(userPostsEntity);
            entityManager.flush();

            trans.commit();
            entityManager.close();

            logger.info("postCommentsEntity id : "+postCommentsEntity.getId());
            return new PostCommentResponse(postCommentsEntity.getId(),postCommentsEntity.getCommentDate());
        }
        catch (Exception  t)
        {
            if ( trans != null && trans.isActive())
            {
                trans.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
            throw t;
        }
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
