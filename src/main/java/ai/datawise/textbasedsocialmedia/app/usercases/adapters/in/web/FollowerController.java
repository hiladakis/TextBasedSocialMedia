package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.FollowerUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.FollowerCommand;
import ai.datawise.textbasedsocialmedia.app.utils.deserializers.FollowerCommandDeserializer;
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
public class FollowerController
{
    FollowerUseCase followerUseCase;

    private static final int statusOk = 200;
    private static final int statusError = 400;
    private static final Logger logger = LogManager.getLogger(FollowerController.class);

    public void followOperation(Context ctx)
    {
        ctx.status(statusError);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(FollowerCommand.class, new FollowerCommandDeserializer());
            Gson gson = gsonBuilder.create();
            FollowerCommand followerCommand = gson.fromJson(ctx.body(), FollowerCommand.class);

            boolean followResponse;
            if( followerCommand.getFollowOperation().equalsIgnoreCase("add"))
            {
                followResponse = followerUseCase.addFollower(followerCommand);
            }
            else{ //operation "remove"
                followResponse = followerUseCase.removeFollower(followerCommand);
            }

            if (followResponse) {
                ctx.status(statusOk);
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
