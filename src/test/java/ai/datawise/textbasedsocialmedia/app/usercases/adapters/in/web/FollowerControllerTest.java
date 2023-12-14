package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.FollowerDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.FollowUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.FollowerService;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.FollowerCommand;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerControllerTest
{
    @Mock
    FollowerDbAdapter followerDbAdapter;
    private FollowerController followerController;
    private FollowerService followerService;
    private static final Logger logger = LogManager.getLogger(FollowerControllerTest.class);

    @Test
    void addFollowerSuccessTest()
    {
        FollowerCommand newFollowerCommand = new FollowerCommand(1, 2, "add");
        followerService = new FollowerService(followerDbAdapter);
        followerController = new FollowerController(followerService);
        FollowUser followUser = newFollowerCommand.getFollowUser();
        when(followerDbAdapter.storeFollower(followUser)).thenReturn(true);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/follow/", ctx -> {
            followerController.followOperation(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/follow/");

            StringEntity entity = new StringEntity(newFollowerCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();

            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
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

        verify(followerDbAdapter, Mockito.times(1)).storeFollower(followUser);
    }

    @Test
    void removeFollowerSuccessTest()
    {
        FollowerCommand newFollowerCommand = new FollowerCommand(1, 2, "remove");
        followerService = new FollowerService(followerDbAdapter);
        followerController = new FollowerController(followerService);
        FollowUser followUser = newFollowerCommand.getFollowUser();
        when(followerDbAdapter.deleteFollower(followUser)).thenReturn(true);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/follow/", ctx -> {
            followerController.followOperation(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/follow/");

            StringEntity entity = new StringEntity(newFollowerCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();
            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
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

        verify(followerDbAdapter, Mockito.times(1)).deleteFollower(followUser);

    }
}