package ai.datawise.textbasedsocialmedia.app.usercases.application.domain.service;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.UserRegistrationDbAdapter;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model.UserRegistrationCommand;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.LimitExceededException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest
{
    @Mock
    EntityManager entityManager;
    @Mock
    private UserRegistrationDbAdapter userRegistrationDbAdapter;

    @Test
    void registerUserTest() throws Exception
    {
        UserRegistrationCommand newUserRegistrationCommand = new UserRegistrationCommand("giannis@example.com", "12345",
                "Free");
        User user = newUserRegistrationCommand.getUser();
        UserRegistrationService userRegistrationService = new UserRegistrationService(userRegistrationDbAdapter);

        DbUtils.EntityManagerFunction<Boolean> entityManagerFunction = entityManager ->
                userRegistrationDbAdapter.storeRegisteredUser(user);

        try (MockedStatic<DbUtils> dbUtilsMockedStatic1 = Mockito.mockStatic(DbUtils.class)) {
            dbUtilsMockedStatic1.when(() -> DbUtils.inTransaction(any())).
                    then(invocation -> entityManagerFunction.apply(entityManager));
            userRegistrationService.registerUser(newUserRegistrationCommand);
            verify(userRegistrationDbAdapter, Mockito.times(1)).storeRegisteredUser(user);
        }
    }

    @Test
    void registerUserWeakPasswordTest()
    {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    try{
                        new UserRegistrationCommand("giannis.hiladakis@example.com", "125",
                                "Free");
                    }
                    catch(ConstraintViolationException ex)
                    {
                        String targetStr = "password must be between 5 and 20 characters";
                        String exceptionStr = ex.getMessage();
                        boolean hasMessage = exceptionStr.contains(targetStr);
                        assertTrue(hasMessage);
                        throw ex;
                    }
                });
    }

    @Test
    void registerUserNullMailTest()
    {

        assertThrows(ConstraintViolationException.class,
                ()->{
                    try{
                        new UserRegistrationCommand(null, "12345",
                                "Free");
                    }
                    catch(ConstraintViolationException ex)
                    {
                        String targetStr = "username must not be null";
                        String exceptionStr = ex.getMessage();
                        boolean hasMessage = exceptionStr.contains(targetStr);
                        assertTrue(hasMessage);
                        throw ex;
                    }
                });
    }

    @Test
    void registerUserInvalidMailTest()
    {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    try{
                        new UserRegistrationCommand("giannis", "12345",
                                "Free");
                    }
                    catch(ConstraintViolationException ex)
                    {
                        String targetStr = "username must be a valid email";
                        String exceptionStr = ex.getMessage();
                        boolean hasMessage = exceptionStr.contains(targetStr);
                        assertTrue(hasMessage);
                        throw ex;
                    }
                });
    }

    @Test
    void registerUserInvalidRoleTest()
    {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    try{
                        new UserRegistrationCommand("giannis@example.com", "12345",
                                "aaaa");
                    }
                    catch(ConstraintViolationException ex)
                    {
                        String targetStr = "user role must be either Free or Premium";
                        String exceptionStr = ex.getMessage();
                        boolean hasMessage = exceptionStr.contains(targetStr);
                        assertTrue(hasMessage);
                        throw ex;
                    }
                });
    }

}