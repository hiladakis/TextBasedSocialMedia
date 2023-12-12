package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserRegistrationCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserRegistrationUseCase;
import ai.datawise.textbasedsocialmedia.app.utils.deserializers.UserRegistrationCommandDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Value
public class UserRegistrationController
{
    UserRegistrationUseCase userRegistrationUseCase;
    private static final int statusOk = 200;
    private static final int statusError = 400;

    private static final Logger logger = LogManager.getLogger(UserRegistrationController.class);

    public void registerUser(Context ctx)
    {
        ctx.status(statusError);
        try
        {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(UserRegistrationCommand.class, new UserRegistrationCommandDeserializer());
            Gson gson = gsonBuilder.create();
            UserRegistrationCommand userRegistrationCommand = gson.fromJson(ctx.body(), UserRegistrationCommand.class);
            boolean isSuccess = userRegistrationUseCase.registerUser(userRegistrationCommand);

            if(isSuccess)
            {
                ctx.status(statusOk);
            }
        }
        catch( Exception  t){
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }
}
