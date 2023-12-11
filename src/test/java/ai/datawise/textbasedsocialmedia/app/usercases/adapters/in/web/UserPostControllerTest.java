package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.UserPostDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.UserPost;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.UserPostService;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserPostCommand;
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
class UserPostControllerTest
{
    @Mock
    UserPostDbAdapter userPostDbAdapter;
    private UserPostController userPostController;
    private UserPostService userPostService;
    private static final Logger logger = LogManager.getLogger(UserPostControllerTest.class);

    @Test
    void userPostControllerMakePostSuccessTest() throws IOException, ParseException
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        Date date = simpleDateFormat.parse(new Timestamp(System.currentTimeMillis()).toString());
        UserPostResponse userPostResponse = new UserPostResponse(1, new Timestamp(date.getTime()));
        UserPostCommand newUserPostCommand = new UserPostCommand(1, "Great day today");
        userPostService = new UserPostService(userPostDbAdapter);
        userPostController = new UserPostController(userPostService);
        UserPost userPost = newUserPostCommand.getUserPost();
        when(userPostDbAdapter.storePost(userPost)).thenReturn(userPostResponse);
        when(userPostDbAdapter.isPremiumUser(1)).thenReturn(false);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/post/", ctx -> {
            userPostController.makePost(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/post/");

            StringEntity entity = new StringEntity(newUserPostCommand.toString());
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
            UserPostResponse userPostRes = gson.fromJson(reader, UserPostResponse.class);

            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertEquals(userPostResponse.getPostId(),userPostRes.getPostId());
            assertEquals(userPostResponse.getPostDate(),userPostRes.getPostDate());
        }
        catch(Exception ex)
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

        verify(userPostDbAdapter, Mockito.times(1)).storePost(userPost);
    }



}