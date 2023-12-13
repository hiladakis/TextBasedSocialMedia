package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

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
    private static final int activeUsersMaxNum = 500;

    private final UserLoginPort userLoginPort;

    @Override
    public LoginResponse loginUser(UserLoginCommand command) throws Exception
    {
        LoginResponse loginResponse = DbUtils.inTransaction(entityManager -> {
            if(userLoginPort.getActiveUsersNum() < activeUsersMaxNum)
            {
                return(userLoginPort.loginUser(command.getLoginUser()));
            }
            throw new LimitExceededException("Active users number is 500 or more");
        });
        return loginResponse;
    }
}
