package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.AuthenticatedUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserLoginPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserLoginDbAdapter implements UserLoginPort
{
    private static final Logger logger = LogManager.getLogger(UserLoginDbAdapter.class);

    @Override
    public LoginResponse loginUser(LoginUser loginUser) throws Exception
    {
        try
        {
            EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
            RegisteredUsersEntity registeredUsersEntity = matchUserCredentials(loginUser, entityManager);
            if( registeredUsersEntity != null)
            {
                AuthenticatedUsersEntity authEntity = getAuthUsersEntityFromRegisteredUser(registeredUsersEntity);
                entityManager.persist(authEntity);
                return new LoginResponse(registeredUsersEntity.getId(), registeredUsersEntity.getUsername(),
                        registeredUsersEntity.getRole());
            }
        }
        catch (Exception t)
        {
            throw t;
        }
        return null;
    }

    @Override
    public Long getActiveUsersNum()
    {
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        String queryStr = "SELECT count(id) FROM giannis.authenticated_users";
        return (Long)entityManager.createNativeQuery(queryStr ).getSingleResult();
    }

    private RegisteredUsersEntity matchUserCredentials(LoginUser loginUser, EntityManager entityManager)
    {
        String queryStr = "SELECT id FROM giannis.registered_users WHERE username='"+loginUser.getUsername()+"'"
                +" AND password='"+loginUser.getPassword()+"'";

        Object registeredUserId = entityManager.createNativeQuery(queryStr ).getSingleResult();
        return entityManager.find(RegisteredUsersEntity.class, registeredUserId);
    }

    private AuthenticatedUsersEntity getAuthUsersEntityFromRegisteredUser(RegisteredUsersEntity registeredUsersEntity)
    {
        AuthenticatedUsersEntity authenticatedUsersEntity = new AuthenticatedUsersEntity();
        authenticatedUsersEntity.setUsername(registeredUsersEntity.getUsername());
        authenticatedUsersEntity.setRole(registeredUsersEntity.getRole());
        authenticatedUsersEntity.setLoginDate(new Timestamp(System.currentTimeMillis()));
        return authenticatedUsersEntity;
    }
}
