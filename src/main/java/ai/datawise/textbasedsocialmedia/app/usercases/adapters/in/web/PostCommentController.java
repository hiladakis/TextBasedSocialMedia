package ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.PostCommentResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.PostCommentCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.PostCommentUseCase;
import ai.datawise.textbasedsocialmedia.app.utils.deserializers.PostCommmentCommandDeserializer;
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
public class PostCommentController
{
    PostCommentUseCase postCommentUseCase;

    private static final int statusOk = 200;
    private static final int statusError = 400;
    private static final Logger logger = LogManager.getLogger(PostCommentController.class);

    public void postComment(Context ctx)
    {
        ctx.status(statusError);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(PostCommentCommand.class, new PostCommmentCommandDeserializer());
            Gson gson = gsonBuilder.create();
            PostCommentCommand postCommentCommand = gson.fromJson(ctx.body(), PostCommentCommand.class);

            PostCommentResponse postCommentResponse = postCommentUseCase.postComment(postCommentCommand);

            if (postCommentResponse != null) {
                ctx.status(statusOk);
                ctx.json(postCommentResponse.toString());
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
