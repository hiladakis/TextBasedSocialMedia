package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserLoginUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserLoginPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserLoginService implements UserLoginUseCase
{
    private static final int activeUsersMaxNum = 500;

    private final UserLoginPort userLoginPort;

    @Override
    public LoginResponse loginUser(UserLoginCommand command) throws Exception
    {
        if(userLoginPort.getActiveUsersNum() < activeUsersMaxNum)
        {
            return(userLoginPort.loginUser(command.getLoginUser()));
        }
        return null;
    }
}
