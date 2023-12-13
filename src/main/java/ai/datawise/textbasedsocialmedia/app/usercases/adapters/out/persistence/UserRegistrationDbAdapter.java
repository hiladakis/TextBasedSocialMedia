package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;
import ai.datawise.textbasedsocialmedia.app.usercases.application.ports.out.UserRegistrationPort;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserRegistrationDbAdapter implements UserRegistrationPort
{
    private static final Logger logger = LogManager.getLogger(UserRegistrationDbAdapter.class);

    @Override
    public boolean storeRegisteredUser(User user) throws Exception
    {
        RegisteredUsersEntity usersEntity = getRegisteredUsersEntityFromUser(user);
        EntityManager entityManager = DbUtils.getEntityManagerThreadLocal().get();
        entityManager.persist(usersEntity);
        return true;
    }

    private RegisteredUsersEntity getRegisteredUsersEntityFromUser(User user)
    {
        RegisteredUsersEntity registeredUsersEntity = new RegisteredUsersEntity();
        registeredUsersEntity.setUsername(user.getUsername());
        registeredUsersEntity.setPassword(user.getPassword());
        registeredUsersEntity.setRole(user.getRole());

        return registeredUsersEntity;
    }
}
