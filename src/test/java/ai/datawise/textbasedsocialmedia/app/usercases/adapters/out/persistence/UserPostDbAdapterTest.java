package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import ai.datawise.textbasedsocialmedia.appconfig.ConfigInstances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserPostDbAdapterTest {

    private static final Logger logger = LogManager.getLogger(UserPostDbAdapterTest.class);
    private static UserPostDbAdapter userPostDbAdapter;
    private UserPost userPost;
    private Integer userId, userIdPremium;
    private String username = "john.hill@gmail.com";
    private String usernamePremium = "jane.hill@gmail.com";
    private String postText = "What a nice day today!";
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUpAll() {
        try {
            entityManagerFactory = ConfigInstances.getEntityManagerFactory();
            userPostDbAdapter = new UserPostDbAdapter();
        } catch (Exception  t) {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    @BeforeEach
    void setUp()
    {
        try (EntityManager em = entityManagerFactory.createEntityManager())
        {
            String queryStr = "SELECT id FROM giannis.registered_users WHERE username='" + username + "'";
            userId = (Integer) em.createNativeQuery(queryStr).getSingleResult();
            userPost = new UserPost(userId, postText);

            String queryStr2 = "SELECT id FROM giannis.registered_users WHERE username='" + usernamePremium + "'";
            userIdPremium = (Integer) em.createNativeQuery(queryStr2).getSingleResult();
        } catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            throw t;
        }
    }

    @Test
    void storePostSuccessTest() throws Exception{
        UserPostResponse userPostResponse =
                DbUtils.inTransaction(entityManager-> userPostDbAdapter.storePost(userPost));
        assertNotNull(userPostResponse);
        assertTrue(userPostResponse.getPostId() > 0);
        assertNotNull(userPostResponse.getPostDate());

        EntityTransaction entityTransaction = null;

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            entityTransaction = em.getTransaction();
            entityTransaction.begin();

            UserPostsEntity userPostsEntity = em.find(UserPostsEntity.class, userPostResponse.getPostId());

            em.remove(userPostsEntity);
            em.flush();
            entityTransaction.commit();
        } catch (Exception  e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
        }
    }

    @Test
    void isPremiumUserTest() throws Exception
    {
        DbUtils.inTransaction(entityManager -> {
            boolean isPremiumUser1 = userPostDbAdapter.isPremiumUser(userId);
            boolean isPremiumUser2 = userPostDbAdapter.isPremiumUser(userIdPremium);
            assertFalse(isPremiumUser1);
            assertTrue(isPremiumUser2);
            return true;
        });
    }
}