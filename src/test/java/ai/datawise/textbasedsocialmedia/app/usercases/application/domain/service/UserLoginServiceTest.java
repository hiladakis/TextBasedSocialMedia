package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.UserLoginDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest
{
    @Mock
    private UserLoginDbAdapter userLoginDbAdapter;

    @Test
    void loginUserSuccessTest()
    {
        LoginResponse loginResponse = new LoginResponse(1,"giannis@example.com","Free");
        UserLoginCommand userLoginCommand = new UserLoginCommand("giannis@example.com", "12345");
        LoginUser loginUser = userLoginCommand.getLoginUser();
        when(userLoginDbAdapter.loginUser(loginUser)).thenReturn(loginResponse);
        long activeUsersNum = 499;
        when(userLoginDbAdapter.getActiveUsersNum()).thenReturn(activeUsersNum);
        UserLoginService userLoginService = new UserLoginService(userLoginDbAdapter);
        LoginResponse loginRes = userLoginService.loginUser(userLoginCommand);
        verify(userLoginDbAdapter, Mockito.times(1)).loginUser(loginUser);
        assertNotNull(loginRes);
        assertEquals(loginResponse.getUserId(),loginRes.getUserId());
        assertEquals(loginResponse.getUsername(),loginRes.getUsername());
        assertEquals(loginResponse.getRole(),loginRes.getRole());
    }

    @Test
    void loginUserFailureTest()
    {
        UserLoginCommand userLoginCommand = new UserLoginCommand("giannis@example.com", "12345");
        LoginUser loginUser = userLoginCommand.getLoginUser();
        long activeUsersNum = 500;
        when(userLoginDbAdapter.getActiveUsersNum()).thenReturn(activeUsersNum);
        UserLoginService userLoginService = new UserLoginService(userLoginDbAdapter);
        LoginResponse loginRes = userLoginService.loginUser(userLoginCommand);
        verify(userLoginDbAdapter, Mockito.times(0)).loginUser(loginUser);
        assertNull(loginRes);
    }
}