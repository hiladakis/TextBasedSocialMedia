package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FollowerDbAdapterTest
{
    private static final Logger logger = LogManager.getLogger(FollowerDbAdapterTest.class);
    private static FollowerDbAdapter followerDbAdapter;
    private static Integer follower1UserId, follower2UserId, followedUserId;
    private static String follower1Username = "john.hill@gmail.com";
    private static String follower2Username = "jack.hill@gmail.com";

    private static String followedUsername = "jane.hill@gmail.com";

    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUpAll() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnit");
            followerDbAdapter = new FollowerDbAdapter();
            followerDbAdapter.setEntityManagerFactory(entityManagerFactory);
        } catch (Throwable t) {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }

        try (EntityManager em = followerDbAdapter.getEntityManagerFactory().createEntityManager())
        {
            String queryStr = "SELECT id FROM giannis.registered_users WHERE username='" + follower1Username + "'";
            follower1UserId = (Integer) em.createNativeQuery(queryStr).getSingleResult();

            String queryStr2 = "SELECT id FROM giannis.registered_users WHERE username='" + follower2Username + "'";
            follower2UserId = (Integer) em.createNativeQuery(queryStr2).getSingleResult();

            String queryStr3 = "SELECT id FROM giannis.registered_users WHERE username='" + followedUsername + "'";
            followedUserId = (Integer) em.createNativeQuery(queryStr3).getSingleResult();

        }
        catch (Throwable t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            throw t;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    void storeFollowersSuccessTest()
    {
        FollowUser followUser1 = new FollowUser(follower1UserId, followedUserId);
        FollowUser followUser2 = new FollowUser(follower2UserId, followedUserId);
        boolean follower1Res = followerDbAdapter.storeFollower(followUser1);
        boolean follower2Res = followerDbAdapter.storeFollower(followUser2);

        assertTrue(follower1Res);
        assertTrue(follower2Res);

        try (EntityManager em = followerDbAdapter.getEntityManagerFactory().createEntityManager())
        {
            String queryStr;
            queryStr = "SELECT * FROM giannis.followers WHERE follower_user_id=" + follower1UserId
                    +" AND followed_user_id="+followedUserId;

            Object [] result1 = (Object[]) em.createNativeQuery(queryStr).getSingleResult();

            queryStr = "SELECT * FROM giannis.followers WHERE follower_user_id=" + follower2UserId
                    +" AND followed_user_id="+followedUserId;

            Object [] result2 = (Object[]) em.createNativeQuery(queryStr).getSingleResult();

            assertEquals(result1[1],follower1UserId);
            assertEquals(result1[2],followedUserId);

            assertEquals(result2[1],follower2UserId);
            assertEquals(result2[2],followedUserId);

            RegisteredUsersEntity follower1 = em
                    .find(RegisteredUsersEntity.class, follower1UserId);

            RegisteredUsersEntity follower2 = em
                    .find(RegisteredUsersEntity.class, follower2UserId);

            RegisteredUsersEntity followed = em
                    .find(RegisteredUsersEntity.class, followedUserId);

            assertEquals(0,follower1.getFollowersEntities().size());
            assertEquals(1,follower1.getFollowingEntities().size());

            String followedByFollower1Username = follower1.getFollowingEntities().get(0)
                            .getFollowed().getUsername();

            assertEquals(followedUsername, followedByFollower1Username );

            String follower1OfFollowedUsername = follower1.getFollowingEntities().get(0)
                    .getFollower().getUsername();

            assertEquals(follower1Username, follower1OfFollowedUsername );

            assertEquals(0,follower2.getFollowersEntities().size());
            assertEquals(1,follower2.getFollowingEntities().size());

            String followedByFollower2Username = follower2.getFollowingEntities().get(0)
                    .getFollowed().getUsername();

            assertEquals(followedUsername, followedByFollower2Username );

            String follower2OfFollowedUsername = follower2.getFollowingEntities().get(0)
                    .getFollower().getUsername();

            assertEquals(follower2Username, follower2OfFollowedUsername );

            assertEquals(2, followed.getFollowersEntities().size());
            assertEquals(0, followed.getFollowingEntities().size());

            String followedUsr = followed.getFollowersEntities().get(0)
                    .getFollowed().getUsername();
            assertEquals(followedUsername, followedUsr );

            String followedUsr2 = followed.getFollowersEntities().get(1)
                    .getFollowed().getUsername();
            assertEquals(followedUsername, followedUsr2 );
        }
        catch (Throwable t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            throw t;
        }

        //delete followers added with this test
        followerDbAdapter.deleteFollower(followUser1);
        followerDbAdapter.deleteFollower(followUser2);
    }

    @Test
    void deleteFollowersSuccessTest()
    {
        FollowUser followUser1 = new FollowUser(follower1UserId, followedUserId);
        FollowUser followUser2 = new FollowUser(follower2UserId, followedUserId);

        //store followers first, before deleting
        followerDbAdapter.storeFollower(followUser1);
        followerDbAdapter.storeFollower(followUser2);

        boolean follower1Res = followerDbAdapter.deleteFollower(followUser1);

        assertTrue(follower1Res);

        try (EntityManager em = followerDbAdapter.getEntityManagerFactory().createEntityManager())
        {
            assertThrows(NoResultException.class,
                    ()->{
                        final String queryStr = "SELECT * FROM giannis.followers WHERE follower_user_id=" + follower1UserId
                                +" AND followed_user_id="+followedUserId;
                        em.createNativeQuery(queryStr).getSingleResult();
                    });
            //check that corresponding entities for followUser1 do not exist , while for followUser2 exist
            RegisteredUsersEntity follower1 = em
                    .find(RegisteredUsersEntity.class, follower1UserId);

            RegisteredUsersEntity follower2 = em
                    .find(RegisteredUsersEntity.class, follower2UserId);

            RegisteredUsersEntity followed = em
                    .find(RegisteredUsersEntity.class, followedUserId);

            assertEquals(0,follower1.getFollowersEntities().size());
            assertEquals(0,follower1.getFollowingEntities().size());

            assertEquals(0,follower2.getFollowersEntities().size());
            assertEquals(1,follower2.getFollowingEntities().size());

            assertEquals(1, followed.getFollowersEntities().size());
            assertEquals(0,followed.getFollowingEntities().size());

        }
        catch (Throwable t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            throw t;
        }

        //delete second followUser entity as well
        boolean follower2Res = followerDbAdapter.deleteFollower(followUser2);
        assertTrue(follower2Res);
    }

}