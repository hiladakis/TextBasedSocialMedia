package ai.datawise.textbasedsocialmedia.app.utils.deserializers;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserRegistrationCommand;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UserRegistrationCommandDeserializer implements JsonDeserializer<UserRegistrationCommand>
{
    @Override
    public UserRegistrationCommand deserialize(JsonElement jsonElement, Type type,
                                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        Gson gson = new Gson();
        UserRegistrationCommand userRegistrationCommand = gson.fromJson(jsonElement.toString(), UserRegistrationCommand.class);
        return new UserRegistrationCommand(userRegistrationCommand.getUsername(), userRegistrationCommand.getPassword(), userRegistrationCommand.getRole());
    }
}