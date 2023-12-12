package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.FollowersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.FollowerPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class FollowerDbAdapter implements FollowerPort
{
    @Setter
    @Getter
    private EntityManagerFactory entityManagerFactory = null;
    private static final Logger logger = LogManager.getLogger(FollowerDbAdapter.class);

    @Override
    public boolean storeFollower(FollowUser followUser) throws Exception{
        EntityManager entityManager = null;
        EntityTransaction trans = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();
            trans = entityManager.getTransaction();
            trans.begin();

            RegisteredUsersEntity followerUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, followUser.getFollowerUserId());

            RegisteredUsersEntity followingUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, followUser.getFollowingUserId());

            FollowersEntity followersEntity = new FollowersEntity();
            followerUsersEntity.addFollowing(followersEntity);
            followingUsersEntity.addFollower(followersEntity);

            entityManager.persist(followerUsersEntity);
            entityManager.persist(followingUsersEntity);

            entityManager.flush();
            trans.commit();
            entityManager.close();

            logger.info("followerUsersEntity id : "+followersEntity.getId());
            return true;
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

    @Override
    public boolean deleteFollower(FollowUser followUser) throws Exception {
        EntityManager entityManager = null;
        EntityTransaction trans = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();
            trans = entityManager.getTransaction();
            trans.begin();

            FollowersEntity followersEntity = getFollowersEntity(followUser, entityManager);

            RegisteredUsersEntity followerUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, followUser.getFollowerUserId());

            RegisteredUsersEntity followingUsersEntity = entityManager
                    .find(RegisteredUsersEntity.class, followUser.getFollowingUserId());

            followerUsersEntity.removeFollowing(followersEntity);
            followingUsersEntity.removeFollower(followersEntity);

            entityManager.persist(followerUsersEntity);
            entityManager.persist(followingUsersEntity);

            entityManager.flush();
            trans.commit();
            entityManager.close();

            return true;
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

    private FollowersEntity getFollowersEntity(FollowUser followUser, EntityManager entityManager)
    {
        String queryStr = "SELECT id FROM giannis.followers WHERE follower_user_id="+followUser.getFollowerUserId()+
                " AND followed_user_id="+followUser.getFollowingUserId();

        Object followerId = entityManager.createNativeQuery(queryStr).getSingleResult();
        if( followerId != null )
        {
            FollowersEntity followersEntity = entityManager.find(FollowersEntity.class,followerId);
            if(followersEntity != null)
            {
                return followersEntity;
            }
        }
        return null;
    }
}
