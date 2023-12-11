package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.AuthenticatedUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserLoginPort;
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
    @Setter
    @Getter
    private EntityManagerFactory entityManagerFactory = null;
    private static final Logger logger = LogManager.getLogger(UserLoginDbAdapter.class);

    @Override
    public LoginResponse loginUser(LoginUser loginUser)
    {
        EntityManager entityManager = null;
        EntityTransaction trans = null;
        try
        {
            entityManager = entityManagerFactory.createEntityManager();
            trans = entityManager.getTransaction();
            trans.begin();
            RegisteredUsersEntity registeredUsersEntity = matchUserCredentials(loginUser, entityManager);
            if( registeredUsersEntity != null)
            {
                AuthenticatedUsersEntity authEntity = getAuthUsersEntityFromRegisteredUser(registeredUsersEntity);
                entityManager.persist(authEntity);
                trans.commit();
                entityManager.close();
                return new LoginResponse(registeredUsersEntity.getId(), registeredUsersEntity.getUsername(),
                        registeredUsersEntity.getRole());
            }
            trans.commit();
            entityManager.close();
        }
        catch (Throwable t)
        {
            if ( trans != null && trans.isActive())
            {
                trans.rollback();
            }
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n")));
        }
        finally
        {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return null;
    }

    @Override
    public Long getActiveUsersNum()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String queryStr = "SELECT count(id) FROM giannis.authenticated_users";
        Long registeredUsersNum = (Long)entityManager.createNativeQuery(queryStr ).getSingleResult();
        entityManager.close();
        return registeredUsersNum;
    }

    private RegisteredUsersEntity matchUserCredentials(LoginUser loginUser, EntityManager entityManager)
    {
        String queryStr = "SELECT id FROM giannis.registered_users WHERE username='"+loginUser.getUsername()+"'";

        Object registeredUserId = entityManager.createNativeQuery(queryStr ).getSingleResult();
        if( registeredUserId != null )
        {
            RegisteredUsersEntity registeredUsersEntity = entityManager.find(RegisteredUsersEntity.class,registeredUserId);
            if(registeredUsersEntity != null && loginUser.getPassword().equals(registeredUsersEntity.getPassword()))
            {
                return registeredUsersEntity;
            }
        }
        return null;
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
