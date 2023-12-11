package ai.datawise.textbasedsocialmedia.app.utils.deserializers;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.PostCommentCommand;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PostCommmentCommandDeserializer implements JsonDeserializer<PostCommentCommand> {
    @Override
    public PostCommentCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        Gson gson = new Gson();
        PostCommentCommand postCommentCommand = gson.fromJson(jsonElement.toString(), PostCommentCommand.class);
        return new PostCommentCommand(postCommentCommand.getPostId(), postCommentCommand.getUserId(),
                postCommentCommand.getComment());
    }
}
