package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserRegistrationCommand;

public interface UserRegistrationUseCase
{
    public boolean registerUser(UserRegistrationCommand command);
}
