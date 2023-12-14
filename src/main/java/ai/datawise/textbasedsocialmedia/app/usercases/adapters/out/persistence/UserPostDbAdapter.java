package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserPostPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;

public class UserPostDbAdapter implements UserPostPort
{
    private static final Logger logger = LogManager.getLogger(UserPostDbAdapter.class);

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
    public UserPostResponse storePost(UserPost userPost)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        RegisteredUsersEntity registeredUsersEntity = entityManager
                .find(RegisteredUsersEntity.class,userPost.getUserId());

        UserPostsEntity userPostsEntity = getUserPostsEntityFromUserPost(userPost);
        registeredUsersEntity.addPost(userPostsEntity);
        entityManager.persist(registeredUsersEntity);
        entityManager.flush();

        logger.info("userPost id: " + userPostsEntity.getId());

        return (new UserPostResponse(userPostsEntity.getId()
                ,userPostsEntity.getPostDate()));
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
