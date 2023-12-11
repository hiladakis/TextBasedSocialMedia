package ai.datawise.textbasedsocialmedia.app.utils.deserializers;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UserLoginCommandDeserializer implements JsonDeserializer<UserLoginCommand>
{
    @Override
    public UserLoginCommand deserialize(JsonElement jsonElement, Type type,
                                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        Gson gson = new Gson();
        UserLoginCommand userLoginCommand = gson.fromJson(jsonElement.toString(), UserLoginCommand.class);
        return new UserLoginCommand(userLoginCommand.getUsername(), userLoginCommand.getPassword());
    }
}