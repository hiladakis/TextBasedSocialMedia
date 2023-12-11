package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.LoadUserDataDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views.*;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.LoadUserDataService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadUserDataControllerTest
{
    @Mock
    LoadUserDataDbAdapter loadUserDataDbAdapter;
    private LoadUserDataController loadUserDataController;
    private LoadUserDataService loadUserDataService;
    private static final Logger logger = LogManager.getLogger(LoadUserDataControllerTest.class);

    @Test
    void getFollowingPostsSuccessTest()
    {
        int userId = 1;
        List<FollowerPostView> followerPostViewList = new ArrayList<>();
        followerPostViewList.add(new FollowerPostView(2,new Timestamp(System.currentTimeMillis()),
                        "post1 text", "jane.hill@gmail.com"));
        followerPostViewList.add(new FollowerPostView(3,new Timestamp(System.currentTimeMillis()),
                "post2 text", "jack.hill@gmail.com"));
        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getFollowingPosts(userId)).thenReturn(followerPostViewList);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/data/posts/following/{id}", ctx -> {
            loadUserDataController.getFollowingPosts(ctx);
        });
        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/data/posts/following/1");
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);
            logger.info("getFollowingPostsSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertTrue(responseStr.contains("\"followerName\":\"jack.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"followerName\":\"jane.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"text\":\"post1 text\""));
            assertTrue(responseStr.contains("\"text\":\"post2 text\""));
        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }

        verify(loadUserDataDbAdapter, Mockito.times(1)).getFollowingPosts(userId);
    }

    @Test
    void getUserPostAndLatestCommentsSuccessTest()
    {
        int postId = 1;
        UserPostWithLatestCommentsView userPostWithLatestCommentsView = new UserPostWithLatestCommentsView( 1,
                new Timestamp(System.currentTimeMillis()), "jane.hill@gmail.com", "post text");
        List<PostCommentView> postCommentViewList = new ArrayList<>();
        postCommentViewList.add(new PostCommentView(1,new Timestamp(System.currentTimeMillis()),
                "john.hill@gmail.com", "comment 1 text"));
        postCommentViewList.add(new PostCommentView(2,new Timestamp(System.currentTimeMillis()),
                "jack.hill@gmail.com", "comment 2 text"));
        userPostWithLatestCommentsView.setLatestComments(postCommentViewList);
        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getUserPostAndLatestComments(postId)).thenReturn(userPostWithLatestCommentsView);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/data/posts/latest-comments/{id}", ctx -> {
            loadUserDataController.getUserPostAndLatestComments(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/data/posts/latest-comments/1");
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);
            logger.info("getUserPostAndLatestCommentsSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertTrue(responseStr.contains("\"postUser\":\"jane.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"text\":\"post text\""));
            assertTrue(responseStr.contains("\"postId\":1"));

            assertTrue(responseStr.contains("\"commentUser\":\"john.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"comment\":\"comment 1 text\""));
            assertTrue(responseStr.contains("\"postCommentId\":1"));

            assertTrue(responseStr.contains("\"commentUser\":\"jack.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"comment\":\"comment 2 text\""));
            assertTrue(responseStr.contains("\"postCommentId\":2"));
        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }
        verify(loadUserDataDbAdapter, Mockito.times(1)).getUserPostAndLatestComments(postId);
    }

    @Test
    void getAllPostCommentsSuccessTest()
    {
        int postId = 1;
        List<PostCommentView> postCommentViewList = new ArrayList<>();
        postCommentViewList.add(new PostCommentView(1,new Timestamp(System.currentTimeMillis()),
                "john.hill@gmail.com", "comment 1 text"));
        postCommentViewList.add(new PostCommentView(2,new Timestamp(System.currentTimeMillis()),
                "jack.hill@gmail.com", "comment 2 text"));
        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getAllPostComments(postId)).thenReturn(postCommentViewList);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/data/posts/all-comments/{id}", ctx -> {
            loadUserDataController.getAllPostComments(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/data/posts/all-comments/1");
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);
            logger.info("getAllPostCommentsSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));

            assertTrue(responseStr.contains("\"commentUser\":\"john.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"comment\":\"comment 1 text\""));
            assertTrue(responseStr.contains("\"postCommentId\":1"));

            assertTrue(responseStr.contains("\"commentUser\":\"jack.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"comment\":\"comment 2 text\""));
            assertTrue(responseStr.contains("\"postCommentId\":2"));
        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }

        verify(loadUserDataDbAdapter, Mockito.times(1)).getAllPostComments(postId);
    }

    @Test
    void getLatestCommentsOnAllUserOrFollowingPostsSuccessTest()
    {
        int userId = 1;
        List<PostCommentView> postCommentViewList = new ArrayList<>();
        postCommentViewList.add(new PostCommentView(1,new Timestamp(System.currentTimeMillis()),
                "john.hill@gmail.com", "comment 1 text"));
        postCommentViewList.add(new PostCommentView(2,new Timestamp(System.currentTimeMillis()),
                "jack.hill@gmail.com", "comment 2 text"));
        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getLatestCommentsOnAllUserOrFollowingPosts(userId)).thenReturn(postCommentViewList);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/data/posts/following/latest-comments/{id}", ctx -> {
            loadUserDataController.getLatestCommentsOnAllUserOrFollowingPosts(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/data/posts/following/latest-comments/1");
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);
            logger.info("getLatestCommentsOnAllUserOrFollowingPostsSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));

            assertTrue(responseStr.contains("\"commentUser\":\"john.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"comment\":\"comment 1 text\""));
            assertTrue(responseStr.contains("\"postCommentId\":1"));

            assertTrue(responseStr.contains("\"commentUser\":\"jack.hill@gmail.com\""));
            assertTrue(responseStr.contains("\"comment\":\"comment 2 text\""));
            assertTrue(responseStr.contains("\"postCommentId\":2"));

        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }

        verify(loadUserDataDbAdapter, Mockito.times(1)).
                getLatestCommentsOnAllUserOrFollowingPosts(userId);
    }

    @Test
    void getFollowerListSuccessTest()
    {
        int userId = 1;
        List<FollowerView> followerViewList = new ArrayList<>();
        followerViewList.add(new FollowerView(1,"john.hill@example.com"));
        followerViewList.add(new FollowerView(2,"jack.hill@example.com"));

        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getFollowerList(userId)).thenReturn(followerViewList);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/data/followers/{id}", ctx -> {
            loadUserDataController.getFollowerList(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/data/followers/1");
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);
            logger.info("getFollowerListSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));

            assertTrue(responseStr.contains("\"userId\":1,\"username\":\"john.hill@example.com\""));
            assertTrue(responseStr.contains("\"userId\":2,\"username\":\"jack.hill@example.com\""));
        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }
        verify(loadUserDataDbAdapter, Mockito.times(1)).
                getFollowerList(userId);
    }

    @Test
    void getFollowingListSuccessTest()
    {
        int userId = 1;
        List<FollowerView> followingViewList = new ArrayList<>();
        followingViewList.add(new FollowerView(1,"jane.hill@example.com"));
        followingViewList.add(new FollowerView(2,"jack.daniels@example.com"));

        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getFollowingList(userId)).thenReturn(followingViewList);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/data/following/{id}", ctx -> {
            loadUserDataController.getFollowingList(ctx);
        });
        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/data/following/1");
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);
            logger.info("getFollowingListSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));

            assertTrue(responseStr.contains("\"userId\":1,\"username\":\"jane.hill@example.com\""));
            assertTrue(responseStr.contains("\"userId\":2,\"username\":\"jack.daniels@example.com\""));
        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }
        verify(loadUserDataDbAdapter, Mockito.times(1)).
                getFollowingList(userId);
    }

    @Test
    void getUsersSuccessTest()
    {
        String usernameStr = "jane";
        List<UserView> userViewList = new ArrayList<>();
        userViewList.add(new UserView(1,"jane.hill@example.com"));
        loadUserDataService = new LoadUserDataService(loadUserDataDbAdapter);
        loadUserDataController = new LoadUserDataController(loadUserDataService);
        when(loadUserDataDbAdapter.getUsersByUsernameStr(usernameStr)).thenReturn(userViewList);

        Javalin app = Javalin.create().start(7000);
        app.get("/users/search/{username}", ctx -> {
            loadUserDataController.getUsers(ctx);
        });
        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:7000/users/search/" + usernameStr);
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            String responseStr = gson.fromJson(reader,String.class);

            logger.info("getUsersSuccessTest responseStr: "+responseStr);
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertTrue(responseStr.contains("\"id\":1,\"username\":\"jane.hill@example.com\""));
        }
        catch(Exception ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        finally
        {
            app.close();
        }
        verify(loadUserDataDbAdapter, Mockito.times(1)).
                getUsersByUsernameStr(usernameStr);
    }
}