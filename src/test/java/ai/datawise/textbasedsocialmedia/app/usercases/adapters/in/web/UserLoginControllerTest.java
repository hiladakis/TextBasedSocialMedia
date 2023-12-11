package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.UserLoginDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.UserLoginService;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import com.google.gson.Gson;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginControllerTest
{
    @Mock
    UserLoginDbAdapter userLoginDbAdapter;
    private UserLoginController userLoginController;
    private UserLoginService userLoginService;
    private static final Logger logger = LogManager.getLogger(UserLoginControllerTest.class);

    @Test
    void loginUserIntegrationSuccessTest()
    {
        LoginResponse loginResponse = new LoginResponse(1,"giannis@example.com","Free");
        UserLoginCommand newUserLoginCommand = new UserLoginCommand("giannis@example.com", "12345");
        userLoginService = new UserLoginService(userLoginDbAdapter);
        userLoginController = new UserLoginController(userLoginService);
        LoginUser loginUser = newUserLoginCommand.getLoginUser();
        when(userLoginDbAdapter.loginUser(loginUser)).thenReturn(loginResponse);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/login/", ctx -> {
            userLoginController.loginUser(ctx);
        });

        //check with creating an actual http connection to the endpoint and process response
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/login/");

            StringEntity entity = new StringEntity(newUserLoginCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();
            InputStream inputStream = response.getEntity().getContent();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            LoginResponse loginRes = new Gson().fromJson(reader, LoginResponse.class);

            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertEquals(loginResponse.getUserId(),loginRes.getUserId());
            assertEquals(loginResponse.getUsername(),loginRes.getUsername());
            assertEquals(loginResponse.getRole(),loginRes.getRole());
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

        verify(userLoginDbAdapter, Mockito.times(1)).loginUser(loginUser);
    }

    @Test
    void loginUserInputFailureTest()
    {
        String username = "giannis";
        String password = "12345";
        userLoginService = new UserLoginService(userLoginDbAdapter);
        userLoginController = new UserLoginController(userLoginService);
        LoginUser loginUser = new LoginUser(username,password);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/login/", ctx -> {
            userLoginController.loginUser(ctx);
        });

        //check with creating an actual http connection to the endpoint
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/login/");

            StringEntity entity = new StringEntity("{'username':'"+username+ "',"+ "'password':'"+password+"'}");
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

        verify(userLoginDbAdapter, Mockito.times(0)).loginUser(loginUser);
    }

}