package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.PostCommentDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.PostComment;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.PostCommentService;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.PostCommentCommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCommentControllerTest
{
    @Mock
    PostCommentDbAdapter postCommentDbAdapter;
    private PostCommentController postCommentController;
    private PostCommentService postCommentService;
    private static final Logger logger = LogManager.getLogger(PostCommentControllerTest.class);

    @Test
    void postCommentControllerPostCommentSuccessTest() throws Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        Date date = simpleDateFormat.parse(new Timestamp(System.currentTimeMillis()).toString());
        PostCommentResponse postCommentResponse = new PostCommentResponse(1,
                new Timestamp(date.getTime()));
        PostCommentCommand newPostCommentCommand = new PostCommentCommand(2, 1,
                "What a nice post you made there!");
        postCommentService = new PostCommentService(postCommentDbAdapter);
        postCommentController = new PostCommentController(postCommentService);
        PostComment postComment = newPostCommentCommand.getPostComment();
        when(postCommentDbAdapter.storePostComment(postComment)).thenReturn(postCommentResponse);
        when(postCommentDbAdapter.isPremiumUser(1)).thenReturn(false);
        when(postCommentDbAdapter.getPostCommentsNumber(2, 1)).thenReturn(4);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/post/comment/", ctx -> {
            postCommentController.postComment(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/post/comment/");

            StringEntity entity = new StringEntity(newPostCommentCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();
            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                    .create();
            PostCommentResponse postCommentRes = gson.fromJson(reader, PostCommentResponse.class);

            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertEquals(postCommentResponse.getPostCommentId(), postCommentRes.getPostCommentId());
            assertEquals(postCommentResponse.getCommentDate(), postCommentRes.getCommentDate());
        }
        catch(IOException ex)
        {
            logger.error(ex + Arrays.asList(ex.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            throw ex;
        }
        finally
        {
            app.close();
        }

        verify(postCommentDbAdapter, Mockito.times(1)).storePostComment(postComment);
    }

    @Test
    void postCommentControllerPostCommentLimitExceededTest() throws Exception
    {
        PostCommentCommand newPostCommentCommand = new PostCommentCommand(2, 1,
                "What a nice post you made there!");
        postCommentService = new PostCommentService(postCommentDbAdapter);
        postCommentController = new PostCommentController(postCommentService);
        PostComment postComment = newPostCommentCommand.getPostComment();
        when(postCommentDbAdapter.isPremiumUser(1)).thenReturn(false);
        when(postCommentDbAdapter.getPostCommentsNumber(2, 1)).thenReturn(5);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/post/comment/", ctx -> {
            postCommentController.postComment(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/post/comment/");

            StringEntity entity = new StringEntity(newPostCommentCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();

            assertTrue(response.toString().contains("HTTP/1.1 400 Bad Request"));
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

        verify(postCommentDbAdapter, Mockito.times(0)).storePostComment(postComment);
    }


}