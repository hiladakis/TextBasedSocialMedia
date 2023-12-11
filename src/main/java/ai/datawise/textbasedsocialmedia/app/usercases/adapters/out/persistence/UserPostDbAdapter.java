package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserPostPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserPostDbAdapter implements UserPostPort
{
    private static final Logger logger = LogManager.getLogger(UserPostDbAdapter.class);

    @Setter
    @Getter
    private EntityManagerFactory entityManagerFactory = null;

    @Override
    public boolean isPremiumUser(int user_id)
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
        catch (Throwable t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n")));
        }
        finally
        {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return false;
    }

    @Override
    public UserPostResponse storePost(UserPost userPost)
    {
        EntityManager entityManager = null;
        EntityTransaction trans = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();
            trans = entityManager.getTransaction();
            trans.begin();

            RegisteredUsersEntity registeredUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class,userPost.getUserId());

            UserPostsEntity userPostsEntity = getUserPostsEntityFromUserPost(userPost);
            registeredUsersEntity.addPost(userPostsEntity);
            entityManager.persist(registeredUsersEntity);
            entityManager.flush();

            trans.commit();
            entityManager.close();

            logger.info("userPost id: " + userPostsEntity.getId());

            return (new UserPostResponse(userPostsEntity.getId()
                    ,userPostsEntity.getPostDate()));
        }
        catch (Throwable t)
        {
            if ( trans != null && trans.isActive())
            {
                trans.rollback();
            }
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n")));
        }
        finally
        {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return null;
    }

    private UserPostsEntity getUserPostsEntityFromUserPost(UserPost userPost)
    {
        UserPostsEntity userPostsEntity = new UserPostsEntity();
        userPostsEntity.setUserId(userPost.getUserId());
        userPostsEntity.setText(userPost.getText());
        userPostsEntity.setPostDate(new Timestamp(System.currentTimeMillis()));
        return userPostsEntity;
    }
}
