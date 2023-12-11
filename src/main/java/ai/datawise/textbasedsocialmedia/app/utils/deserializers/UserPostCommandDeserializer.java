package ai.datawise.textbasedsocialmedia.app.utils.deserializers;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserPostCommand;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UserPostCommandDeserializer implements JsonDeserializer<UserPostCommand> {
    @Override
    public UserPostCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new Gson();
        UserPostCommand userPostCommand = gson.fromJson(jsonElement.toString(), UserPostCommand.class);
        return new UserPostCommand(userPostCommand.getUserId(), userPostCommand.getText());
    }
}
