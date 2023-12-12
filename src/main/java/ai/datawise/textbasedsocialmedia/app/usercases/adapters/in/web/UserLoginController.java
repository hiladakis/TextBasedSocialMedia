package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserLoginUseCase;
import ai.datawise.textbasedsocialmedia.app.utils.deserializers.UserLoginCommandDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import jakarta.validation.ConstraintViolationException;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Value
public class UserLoginController
{
    UserLoginUseCase userLoginUseCase;
    private static final int statusOk = 200;
    private static final int statusError = 400;

    private static final Logger logger = LogManager.getLogger(UserLoginController.class);

    public void loginUser(Context ctx)
    {
        ctx.status(statusError);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(UserLoginCommand.class, new UserLoginCommandDeserializer());
            Gson gson = gsonBuilder.create();
            UserLoginCommand userLoginCommand = gson.fromJson(ctx.body(), UserLoginCommand.class);
            LoginResponse loginResponse = userLoginUseCase.loginUser(userLoginCommand);

            if (loginResponse != null) {
                ctx.status(statusOk);
                ctx.json(loginResponse.toString());
            }
        } catch (Exception  t) {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }
}
