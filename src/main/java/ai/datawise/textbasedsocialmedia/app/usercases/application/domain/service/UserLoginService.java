package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.UserLoginUseCase;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserLoginPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import lombok.RequiredArgsConstructor;

import javax.naming.LimitExceededException;

@RequiredArgsConstructor
public class UserLoginService implements UserLoginUseCase
{
    private final UserLoginPort userLoginPort;

    @Override
    public LoginResponse loginUser(UserLoginCommand command) throws Exception
    {
        LoginResponse loginResponse = DbUtils.inTransaction(entityManager -> {
            LoginUser loginUser = command.getLoginUser();
            if(!loginUser.isActiveUsersNumExceeded(userLoginPort.getActiveUsersNum()))
            {
                return(userLoginPort.loginUser(loginUser));
            }
            throw new LimitExceededException("Active users number is 500 or more");
        });
        return loginResponse;
    }
}
