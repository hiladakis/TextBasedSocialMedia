package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.FollowersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.FollowerPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
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
    private static final Logger logger = LogManager.getLogger(FollowerDbAdapter.class);

    @Override
    public boolean storeFollower(FollowUser followUser)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        RegisteredUsersEntity followerUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, followUser.getFollowerUserId());

        RegisteredUsersEntity followingUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, followUser.getFollowingUserId());

        FollowersEntity followersEntity = new FollowersEntity();
        followerUsersEntity.addFollowing(followersEntity);
        followingUsersEntity.addFollower(followersEntity);

        entityManager.persist(followerUsersEntity);
        entityManager.persist(followingUsersEntity);

        logger.info("followerUsersEntity id : "+followersEntity.getId());
        return true;
    }

    @Override
    public boolean deleteFollower(FollowUser followUser)
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();

        FollowersEntity followersEntity = getFollowersEntity(followUser, entityManager);

        RegisteredUsersEntity followerUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, followUser.getFollowerUserId());

        RegisteredUsersEntity followingUsersEntity = entityManager
                .find(RegisteredUsersEntity.class, followUser.getFollowingUserId());

        followerUsersEntity.removeFollowing(followersEntity);
        followingUsersEntity.removeFollower(followersEntity);

        entityManager.persist(followerUsersEntity);
        entityManager.persist(followingUsersEntity);

        return true;
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
