package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
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

class PostCommentDbAdapterTest {
    private static final Logger logger = LogManager.getLogger(PostCommentDbAdapterTest.class);
    private static UserPostDbAdapter userPostDbAdapter;
    private static PostCommentDbAdapter postCommentDbAdapter;
    private static UserPost userPost;
    private static PostComment postComment, postComment2, postComment3;
    private static Integer userId, userIdPremium, postId;
    private static String username = "john.hill@gmail.com";
    private static String usernamePremium = "jane.hill@gmail.com";
    private static String postText = "What a nice day today!";
    private static String postCommentText = "It's indeed a great day!";
    private static String postCommentText2 = "I am feeling great!";
    private static String postCommentText3 = "Wow great energy guys!";

    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUpAll() throws Exception{
        try {
            entityManagerFactory = ConfigInstances.getEntityManagerFactory();
            userPostDbAdapter = new UserPostDbAdapter();
            postCommentDbAdapter = new PostCommentDbAdapter();
        } catch (Exception  t) {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            String queryStr = "SELECT id FROM giannis.registered_users WHERE username='" + username + "'";
            userId = (Integer) em.createNativeQuery(queryStr).getSingleResult();
            userPost = new UserPost(userId, postText);

            String queryStr2 = "SELECT id FROM giannis.registered_users WHERE username='" + usernamePremium + "'";
            userIdPremium = (Integer) em.createNativeQuery(queryStr2).getSingleResult();

            UserPostResponse userPostResponse = DbUtils.
                    inTransaction(entityManager -> userPostDbAdapter.storePost(userPost));
            postId = userPostResponse.getPostId();

            postComment = new PostComment(postId, userId, postCommentText);
            postComment2 = new PostComment(postId, userIdPremium, postCommentText2);
            postComment3 = new PostComment(postId, userIdPremium, postCommentText3);

        } catch (Exception  t) {
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

        EntityTransaction entityTransaction = null;

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            entityTransaction = em.getTransaction();
            entityTransaction.begin();

            UserPostsEntity userPostsEntity = em.find(UserPostsEntity.class, postId);

            em.remove(userPostsEntity);
            em.flush();
            entityTransaction.commit();
        } catch (Exception  e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        }
    }

    @Test
    void isPremiumUserTest() throws Exception{
        DbUtils.inTransaction(entityManager -> {
            boolean isPremiumUser1 = postCommentDbAdapter.isPremiumUser(userId);
            boolean isPremiumUser2 = postCommentDbAdapter.isPremiumUser(userIdPremium);
            assertFalse(isPremiumUser1);
            assertTrue(isPremiumUser2);
            return true;
        });
    }


    @Test
    void storePostCommentSuccessTest() throws Exception{
        DbUtils.inTransaction(entityManager ->
        {
            PostCommentResponse postCommentResponse = postCommentDbAdapter.storePostComment(postComment);
            assertNotNull(postCommentResponse);
            assertTrue(postCommentResponse.getPostCommentId() > 0);
            assertNotNull(postCommentResponse.getCommentDate());

            PostCommentResponse postCommentResponse2 = postCommentDbAdapter.storePostComment(postComment2);
            assertNotNull(postCommentResponse2);
            assertTrue(postCommentResponse2.getPostCommentId() > 0);
            assertNotNull(postCommentResponse2.getCommentDate());

            PostCommentResponse postCommentResponse3 = postCommentDbAdapter.storePostComment(postComment3);
            assertNotNull(postCommentResponse3);
            assertTrue(postCommentResponse3.getPostCommentId() > 0);
            assertNotNull(postCommentResponse3.getCommentDate());

            assertEquals(1, postCommentDbAdapter.getPostCommentsNumber(postId, userId));
            assertEquals(2, postCommentDbAdapter.getPostCommentsNumber(postId, userIdPremium));
            return true;
        });

        try (EntityManager em = entityManagerFactory.createEntityManager())
        {
            RegisteredUsersEntity postCommentUser1 = em
                    .find(RegisteredUsersEntity.class, userId);

            RegisteredUsersEntity postCommentUser2 = em
                    .find(RegisteredUsersEntity.class, userIdPremium);

            assertEquals(1, postCommentUser1.getUserPosts().size());
            assertEquals(0, postCommentUser2.getUserPosts().size());
            assertEquals(3, postCommentUser1.getUserPosts().get(0).getPostComments().size());
        }
        catch (Exception  t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            throw t;
        }
    }
}