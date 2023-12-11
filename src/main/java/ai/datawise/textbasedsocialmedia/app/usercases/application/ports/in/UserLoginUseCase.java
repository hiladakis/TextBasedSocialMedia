package ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;

public interface UserLoginUseCase
{
    public LoginResponse loginUser(UserLoginCommand command);
}
