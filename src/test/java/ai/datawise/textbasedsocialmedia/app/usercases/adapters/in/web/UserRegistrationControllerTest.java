package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.UserRegistrationDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service.UserRegistrationService;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserRegistrationCommand;
import io.javalin.Javalin;
import jakarta.validation.ConstraintViolationException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationControllerTest {

    @Mock
    UserRegistrationDbAdapter userRegistrationDbAdapter;
    private UserRegistrationController userRegistrationController;
    private UserRegistrationService userRegistrationService;

    private static final Logger logger = LogManager.getLogger(UserRegistrationControllerTest.class);

    @Test
    void userRegistrationControllerIntegrationSuccessTest()
    {
        //setUp
        UserRegistrationCommand newUserRegistrationCommand = new UserRegistrationCommand("giannis@example.com", "12345",
                "Free");
        userRegistrationService = new UserRegistrationService(userRegistrationDbAdapter);
        userRegistrationController = new UserRegistrationController(userRegistrationService);
        User user = newUserRegistrationCommand.getUser();
        when(userRegistrationDbAdapter.storeRegisteredUser(user)).thenReturn(true);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/register/", ctx -> {
            userRegistrationController.registerUser(ctx);
        });

        //check with creating an actual http connection to the endpoint
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/register/");

            StringEntity entity = new StringEntity(newUserRegistrationCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();

            assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
            assertFalse(response.toString().contains("HTTP/1.1 400 Bad Request"));

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

        verify(userRegistrationDbAdapter,Mockito.times(1)).storeRegisteredUser(user);
    }

    @Test
    void userRegistrationControllerIntegrationFailureTest()
    {
        //setUp
        UserRegistrationCommand newUserRegistrationCommand = new UserRegistrationCommand("giannis@example.com", "12345",
                "Free");
        userRegistrationService = new UserRegistrationService(userRegistrationDbAdapter);
        userRegistrationController = new UserRegistrationController(userRegistrationService);
        User user = newUserRegistrationCommand.getUser();
        when(userRegistrationDbAdapter.storeRegisteredUser(user)).thenReturn(false);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/register/", ctx -> {
            userRegistrationController.registerUser(ctx);
        });

        //check with creating an actual http connection to the endpoint
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/register/");

            StringEntity entity = new StringEntity(newUserRegistrationCommand.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();

            assertFalse(response.toString().contains("HTTP/1.1 200 OK"));
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

        verify(userRegistrationDbAdapter,Mockito.times(1)).storeRegisteredUser(user);
    }

    @Test
    void userRegistrationControllerInputFailureTest()
    {
        //setUp
        String username = "giannis";
        String password = "12345";
        String role = "Free";
        userRegistrationService = new UserRegistrationService(userRegistrationDbAdapter);
        userRegistrationController = new UserRegistrationController(userRegistrationService);
        User user = new User(username,password,role);

        Javalin app = Javalin.create().start(7000);
        app.post("/users/register/", ctx -> {
            userRegistrationController.registerUser(ctx);
        });

        //check with creating an actual http connection to the endpoint
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:7000/users/register/");

            StringEntity entity = new StringEntity("{'username':'"+username+ "',"+ "'password':'"+password+"','role':'"+role+"'}");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();

            assertFalse(response.toString().contains("HTTP/1.1 200 OK"));
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

        verify(userRegistrationDbAdapter,Mockito.times(0)).storeRegisteredUser(user);
    }


}