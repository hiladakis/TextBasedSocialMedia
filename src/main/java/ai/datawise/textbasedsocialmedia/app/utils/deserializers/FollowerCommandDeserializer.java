package ai.datawise.textbasedsocialmedia.app.utils.deserializers;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.FollowerCommand;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FollowerCommandDeserializer implements JsonDeserializer<FollowerCommand> {
    @Override
    public FollowerCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        Gson gson = new Gson();
        FollowerCommand followerCommand = gson.fromJson(jsonElement.toString(), FollowerCommand.class);
        return new FollowerCommand(followerCommand.getFollowerUserId(), followerCommand.getFollowedUserId(),
                followerCommand.getFollowOperation());
    }
}