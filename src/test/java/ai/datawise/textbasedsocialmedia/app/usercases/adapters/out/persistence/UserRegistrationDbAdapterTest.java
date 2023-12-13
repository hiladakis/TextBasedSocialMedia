package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;
import ai.datawise.textbasedsocialmedia.app.utils.DbUtils;
import ai.datawise.textbasedsocialmedia.appconfig.ConfigInstances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationDbAdapterTest
{
    private UserRegistrationDbAdapter userRegistrationDbAdapter;
    private User user;
    private static final Logger logger = LogManager.getLogger(UserRegistrationDbAdapterTest.class);

    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUpAll()
    {
        try{
            entityManagerFactory = ConfigInstances.getEntityManagerFactory();
        }
        catch(Exception  t){
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    @BeforeEach
    void setUp(TestInfo info)
    {
        if(info.getDisplayName().equals("storeRegisteredUserIntegrationSuccessTest()"))
        {
            try{
                userRegistrationDbAdapter = new UserRegistrationDbAdapter();
                user = new User("giannis.hiladakis@gmail.com","15984","Free");
            }
            catch(Exception  t)
            {
                logger.error(t + Arrays.asList(t.getStackTrace())
                        .stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"))
                );
                throw t;
            }
        }
    }

    @AfterEach
    void tearDown(TestInfo info)
    {
        if(info.getDisplayName().equals("storeRegisteredUserIntegrationSuccessTest()"))
        {
            try(EntityManager em = entityManagerFactory.createEntityManager())
            {
                String queryStr = "SELECT id FROM giannis.registered_users WHERE username='"+user.getUsername()+"'";
                Object registeredUserId = em.createNativeQuery(queryStr ).getSingleResult();
                RegisteredUsersEntity registeredUsersEntity = em.find(RegisteredUsersEntity.class,registeredUserId);
                EntityTransaction entityTransaction = em.getTransaction();
                entityTransaction.begin();
                em.remove(registeredUsersEntity);
                em.flush();
                entityTransaction.commit();
            }
        }
    }


    @Test
    void storeRegisteredUserIntegrationSuccessTest() throws Exception
    {
        DbUtils.inTransaction(entityManager -> userRegistrationDbAdapter.storeRegisteredUser(user));
        try(EntityManager em = entityManagerFactory.createEntityManager())
        {
            String queryStr = "SELECT id FROM giannis.registered_users WHERE username='"+user.getUsername()+"'";
            Object registeredUserId = em.createNativeQuery(queryStr ).getSingleResult();
            RegisteredUsersEntity registeredUsersEntity = em.find(RegisteredUsersEntity.class,registeredUserId);

            assertEquals(user.getUsername(), registeredUsersEntity.getUsername());
            assertEquals(user.getPassword(), registeredUsersEntity.getPassword());
            assertEquals(user.getRole(), registeredUsersEntity.getRole());
            assertNotEquals("Premium", registeredUsersEntity.getRole());
            assertEquals(0, registeredUsersEntity.getVersion());
        }
    }
}