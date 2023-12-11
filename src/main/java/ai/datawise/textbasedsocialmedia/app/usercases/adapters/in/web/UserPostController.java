package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.UserPostResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserPostCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserPostUseCase;
import ai.datawise.textbasedsocialmedia.app.utils.deserializers.UserPostCommandDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Value
public class UserPostController
{
    UserPostUseCase userPostUseCase;
    private static final int statusOk = 200;
    private static final int statusError = 400;
    private static final Logger logger = LogManager.getLogger(UserPostController.class);

    public void makePost(Context ctx)
    {
        ctx.status(statusError);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(UserPostCommand.class, new UserPostCommandDeserializer());
            Gson gson = gsonBuilder.create();
            UserPostCommand userPostCommand = gson.fromJson(ctx.body(), UserPostCommand.class);

            UserPostResponse userPostResponse = userPostUseCase.makePost(userPostCommand);

            if (userPostResponse != null) {
                ctx.status(statusOk);
                ctx.json(userPostResponse.toString());
            }
        } catch (Throwable t) {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }
}
