package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.PostCommentsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.UserPostsEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LoadUserDataDbAdapterTest
{
    private static final Logger logger = LogManager.getLogger(LoadUserDataDbAdapterTest.class);
    private static LoadUserDataDbAdapter loadUserDataDbAdapter;
    private static FollowerDbAdapter followerDbAdapter;
    private static PostCommentDbAdapter postCommentDbAdapter;
    private static FollowUser followUser1, followUser2;
    private static UserPostDbAdapter userPostDbAdapter;
    private static Integer follower1UserId, follower2UserId, followedUserId, postId, postId2, postId3, postId4;
    private static String follower1Username = "john.hill@gmail.com";
    private static String follower2Username = "jack.hill@gmail.com";
    private static String followedUsername = "jane.hill@gmail.com";
    private static UserPost userPost, userPost2, userPost3, userPost4;
    private static PostComment postComment, postComment2, postComment3, post2Comment, post2Comment2, post3Comment;
    private static PostComment post4Comment;
    private static String postText = "What a nice day today!", post2Text = "Good morning everyone!";
    private static String post3Text = "Let's all hope global peace prevails", post4Text = "Anyone interested in chatting?";
    private static String postCommentText = "It's indeed a great day!", post2CommentText = "Good morning mate";
    private static String postCommentText2 = "I am feeling great!", post2CommentText2 = "Greetings from Chania!";
    private static String postCommentText3 = "Wow great energy guys!", post3CommentText = "Amen to that";
    private static String post4CommentText = "I am interested!";


    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUpAll() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnit");
            followerDbAdapter = new FollowerDbAdapter();
            userPostDbAdapter = new UserPostDbAdapter();
            postCommentDbAdapter = new PostCommentDbAdapter();
            loadUserDataDbAdapter = new LoadUserDataDbAdapter();
            followerDbAdapter.setEntityManagerFactory(entityManagerFactory);
            userPostDbAdapter.setEntityManagerFactory(entityManagerFactory);
            postCommentDbAdapter.setEntityManagerFactory(entityManagerFactory);
            loadUserDataDbAdapter.setEntityManagerFactory(entityManagerFactory);
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

            followUser1 = new FollowUser(follower1UserId, followedUserId);
            followUser2 = new FollowUser(follower2UserId, followedUserId);
            followerDbAdapter.storeFollower(followUser1);
            followerDbAdapter.storeFollower(followUser2);
            //first post made by follower1
            userPost = new UserPost(follower1UserId, postText);
            UserPostResponse userPostResponse = userPostDbAdapter.storePost(userPost);
            postId = userPostResponse.getPostId();

            postComment = new PostComment(postId, followedUserId, postCommentText);
            postComment2 = new PostComment(postId, follower2UserId, postCommentText2);
            postComment3 = new PostComment(postId, followedUserId, postCommentText3);

            postCommentDbAdapter.storePostComment(postComment);
            postCommentDbAdapter.storePostComment(postComment2);
            postCommentDbAdapter.storePostComment(postComment3);
            //second post made by followed user
            userPost2 = new UserPost(followedUserId, post2Text);
            UserPostResponse userPostResponse2 = userPostDbAdapter.storePost(userPost2);
            postId2 = userPostResponse2.getPostId();

            post2Comment = new PostComment(postId2, follower2UserId, post2CommentText);
            post2Comment2 = new PostComment(postId2, followedUserId, post2CommentText2);

            postCommentDbAdapter.storePostComment(post2Comment);
            postCommentDbAdapter.storePostComment(post2Comment2);
            //third post made by followed user
            userPost3 = new UserPost(followedUserId, post3Text);
            UserPostResponse userPostResponse3 = userPostDbAdapter.storePost(userPost3);
            postId3 = userPostResponse3.getPostId();
            post3Comment = new PostComment(postId3, follower1UserId, post3CommentText);
            postCommentDbAdapter.storePostComment(post3Comment);
            //fourth post made by followed user
            userPost4 = new UserPost(followedUserId, post4Text);
            UserPostResponse userPostResponse4 = userPostDbAdapter.storePost(userPost4);
            postId4 = userPostResponse4.getPostId();
            post4Comment = new PostComment(postId4, follower1UserId, post4CommentText);
            postCommentDbAdapter.storePostComment(post4Comment);
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
        followerDbAdapter.deleteFollower(followUser1);
        followerDbAdapter.deleteFollower(followUser2);

        EntityTransaction entityTransaction = null;

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            entityTransaction = em.getTransaction();
            entityTransaction.begin();

            UserPostsEntity userPostsEntity1 = em.find(UserPostsEntity.class, postId);
            em.remove(userPostsEntity1);
            UserPostsEntity userPostsEntity2 = em.find(UserPostsEntity.class, postId2);
            em.remove(userPostsEntity2);
            UserPostsEntity userPostsEntity3 = em.find(UserPostsEntity.class, postId3);
            em.remove(userPostsEntity3);
            UserPostsEntity userPostsEntity4 = em.find(UserPostsEntity.class, postId4);
            em.remove(userPostsEntity4);
            em.flush();
            entityTransaction.commit();
        } catch (Throwable e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
        }

        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }


    @Test
    void getFollowingPostsSuccessTest()
    {
        List<FollowerPostView> followerPostViewList = loadUserDataDbAdapter.getFollowingPosts(follower1UserId);
        assertEquals(3, followerPostViewList.size());
        //reverse chronological order
        assertTrue(followerPostViewList.get(0).getPostDate().
                compareTo(followerPostViewList.get(1).getPostDate())>0);
        assertTrue(followerPostViewList.get(1).getPostDate().
                compareTo(followerPostViewList.get(2).getPostDate())>0);

        assertEquals(followedUsername, followerPostViewList.get(0).getFollowerName());
    }

    @Test
    void getUserPostAndLatestCommentsSuccessTest()
    {
        UserPostWithLatestCommentsView userPostWithLatestCommentsView =
                loadUserDataDbAdapter.getUserPostAndLatestComments(postId3);
        assertEquals(followedUsername, userPostWithLatestCommentsView.getPostUser());
        assertEquals(post3Text, userPostWithLatestCommentsView.getText());
        assertEquals(1, userPostWithLatestCommentsView.getLatestComments().size());
        assertEquals(post3CommentText,userPostWithLatestCommentsView.getLatestComments().get(0).getComment());

        UserPostWithLatestCommentsView userPostWithLatestCommentsView2 =
                loadUserDataDbAdapter.getUserPostAndLatestComments(postId);
        assertEquals(follower1Username, userPostWithLatestCommentsView2.getPostUser());
        assertEquals(3, userPostWithLatestCommentsView2.getLatestComments().size());
        //test reverse chronological order
        assertTrue(userPostWithLatestCommentsView2.getLatestComments().get(0).getCommentDate().
                compareTo(userPostWithLatestCommentsView2.getLatestComments().get(1).getCommentDate() )> 0);
        assertTrue(userPostWithLatestCommentsView2.getLatestComments().get(1).getCommentDate().
                compareTo(userPostWithLatestCommentsView2.getLatestComments().get(2).getCommentDate() )> 0);
    }

    @Test
    void getAllPostCommentsSuccessTest()
    {
        List<PostCommentView> postCommentViewList = loadUserDataDbAdapter.getAllPostComments(postId);
        assertEquals(3, postCommentViewList.size());
    }

    @Test
    void getLatestCommentsOnAllUserOrFollowingPostsSuccessTest()
    {
        List<PostCommentView> postCommentViewList = loadUserDataDbAdapter.
                getLatestCommentsOnAllUserOrFollowingPosts(follower1UserId);
        assertEquals(7, postCommentViewList.size());
        //test reverse chronological order
        assertTrue(postCommentViewList.get(0).getCommentDate().
                compareTo(postCommentViewList.get(1).getCommentDate())>0);
        assertTrue(postCommentViewList.get(1).getCommentDate().
                compareTo(postCommentViewList.get(2).getCommentDate())>0);
        assertTrue(postCommentViewList.get(2).getCommentDate().
                compareTo(postCommentViewList.get(3).getCommentDate())>0);
        assertTrue(postCommentViewList.get(3).getCommentDate().
                compareTo(postCommentViewList.get(4).getCommentDate())>0);
        assertTrue(postCommentViewList.get(4).getCommentDate().
                compareTo(postCommentViewList.get(5).getCommentDate())>0);
        assertTrue(postCommentViewList.get(5).getCommentDate().
                compareTo(postCommentViewList.get(6).getCommentDate())>0);
    }

    @Test
    void getFollowerListSuccessTest()
    {
        List<FollowerView> followerViewList = loadUserDataDbAdapter.getFollowerList(followedUserId);
        assertEquals(2, followerViewList.size());

        List<FollowerView> followerViewList2 = loadUserDataDbAdapter.getFollowerList(follower1UserId);
        assertEquals(0, followerViewList2.size());

        List<FollowerView> followerViewList3 = loadUserDataDbAdapter.getFollowerList(follower2UserId);
        assertEquals(0, followerViewList3.size());

    }

    @Test
    void getFollowingListSuccessTest()
    {
        List<FollowerView> followingViewList = loadUserDataDbAdapter.getFollowingList(follower1UserId);
        assertEquals(1, followingViewList.size());
        assertEquals(followedUsername, followingViewList.get(0).getUsername());

        List<FollowerView> followingViewList2 = loadUserDataDbAdapter.getFollowingList(follower2UserId);
        assertEquals(1, followingViewList2.size());
        assertEquals(followedUsername, followingViewList2.get(0).getUsername());

        List<FollowerView> followingViewList3 = loadUserDataDbAdapter.getFollowingList(followedUserId);
        assertEquals(0, followingViewList3.size());
    }

    @Test
    public void getUsersByUsernameStrSuccessTest()
    {
        String usernameStr = "hill";
        List<UserView> userViewList = loadUserDataDbAdapter.getUsersByUsernameStr(usernameStr);
        assertEquals(4, userViewList.size());

        Predicate<UserView> usernamePredicate = userView -> userView
                .getUsername().contains(usernameStr);

        String userViewListStr = userViewList.stream().filter(usernamePredicate).toList().toString();

        assertTrue(userViewListStr.contains("john.hill@gmail.com"));
        assertTrue(userViewListStr.contains("jack.hill@gmail.com"));
        assertTrue(userViewListStr.contains("jane.hill@gmail.com"));
        assertTrue(userViewListStr.contains("george.hill@example.com"));
    }

}