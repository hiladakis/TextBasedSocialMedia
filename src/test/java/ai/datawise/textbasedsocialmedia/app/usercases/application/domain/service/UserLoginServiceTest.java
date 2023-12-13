package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.UserLoginDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserLoginCommand;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserLoginPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.LimitExceededException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest
{
    @Mock
    EntityManager entityManager;
    @Mock
    private UserLoginPort userLoginPort;

    @Test
    void loginUserSuccessTest() throws Exception
    {
        LoginResponse loginResponse = new LoginResponse(1,"giannis@example.com","Free");
        UserLoginCommand userLoginCommand = new UserLoginCommand("giannis@example.com", "12345");
        LoginUser loginUser = userLoginCommand.getLoginUser();
        when(userLoginPort.loginUser(loginUser)).thenReturn(loginResponse);
        long activeUsersNum = 499;
        when(userLoginPort.getActiveUsersNum()).thenReturn(activeUsersNum);
        UserLoginService userLoginService = new UserLoginService(userLoginPort);
        DbUtils.EntityManagerFunction<LoginResponse> entityManagerFunction = entityManager -> {
            if (userLoginPort.getActiveUsersNum() < 500) {
                return (userLoginPort.loginUser(userLoginCommand.getLoginUser()));
            }
            throw new LimitExceededException("Active users number is 500 or more");
        };
        try (MockedStatic<DbUtils> dbUtilsMockedStatic1 = Mockito.mockStatic(DbUtils.class))
        {
            dbUtilsMockedStatic1.when(() -> DbUtils.inTransaction(any())).
                    then(invocation -> entityManagerFunction.apply(entityManager));

            LoginResponse loginRes = userLoginService.loginUser(userLoginCommand);
            assertNotNull(loginRes);
            verify(userLoginPort, times(1)).loginUser(userLoginCommand.getLoginUser());
            assertEquals(loginResponse.getUserId(),loginRes.getUserId());
            assertEquals(loginResponse.getUsername(),loginRes.getUsername());
            assertEquals(loginResponse.getRole(),loginRes.getRole());
        }
    }

    @Test
    void loginUserFailureTest() throws Exception
    {
        UserLoginCommand userLoginCommand = new UserLoginCommand("giannis@example.com", "12345");
        LoginUser loginUser = userLoginCommand.getLoginUser();
        long activeUsersNum = 500;
        when(userLoginPort.getActiveUsersNum()).thenReturn(activeUsersNum);
        UserLoginService userLoginService = new UserLoginService(userLoginPort);

        DbUtils.EntityManagerFunction<LoginResponse> entityManagerFunction = entityManager -> {
            if (userLoginPort.getActiveUsersNum() < 500) {
                return (userLoginPort.loginUser(userLoginCommand.getLoginUser()));
            }
            throw new LimitExceededException("Active users number is 500 or more");
        };
        try (MockedStatic<DbUtils> dbUtilsMockedStatic1 = Mockito.mockStatic(DbUtils.class))
        {
            dbUtilsMockedStatic1.when(() -> DbUtils.inTransaction(any())).
                    then(invocation -> entityManagerFunction.apply(entityManager));

            assertThrows(LimitExceededException.class, () -> {
                userLoginService.loginUser(userLoginCommand);
            });
            verify(userLoginPort, times(0)).loginUser(loginUser);
        }
    }
}