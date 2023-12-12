package ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.AuthenticatedUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.adapters.out.persistence.model.RegisteredUsersEntity;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses.LoginResponse;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.LoginUser;
import ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginDbAdapterTest
{
    private UserLoginDbAdapter userLoginDbAdapter;
    private LoginUser loginUser;
    private static final Logger logger = LogManager.getLogger(UserLoginDbAdapterTest.class);

    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUpAll()
    {
        try{
            entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnit");
        }
        catch(Exception  t){
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );

            logger.error(t);
        }
    }

    @AfterAll
    static void tearDownAll()
    {
        if(entityManagerFactory != null)
        {
            entityManagerFactory.close();
        }
    }

    @BeforeEach
    void setUp(TestInfo info) throws Exception
    {
        if(info.getDisplayName().equals("loginUserIntegrationSuccessTest()"))
        {
            try{
                userLoginDbAdapter = new UserLoginDbAdapter();

                userLoginDbAdapter.setEntityManagerFactory(entityManagerFactory);
                loginUser = new LoginUser("irene@gmail.com","15984");

                UserRegistrationDbAdapter userRegistrationDbAdapter = new UserRegistrationDbAdapter();
                userRegistrationDbAdapter.setEntityManagerFactory(entityManagerFactory);
                User user = new User("irene@gmail.com","15984","Free");
                userRegistrationDbAdapter.storeRegisteredUser(user);

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
        if(info.getDisplayName().equals("loginUserIntegrationFailureTest()"))
        {
            try{
                userLoginDbAdapter = new UserLoginDbAdapter();
                userLoginDbAdapter.setEntityManagerFactory(entityManagerFactory);
                loginUser = new LoginUser("irene@gmail.com","15984");

                UserRegistrationDbAdapter userRegistrationDbAdapter = new UserRegistrationDbAdapter();
                userRegistrationDbAdapter.setEntityManagerFactory(entityManagerFactory);
                User user = new User("irene@gmail.com","15985","Free");
                userRegistrationDbAdapter.storeRegisteredUser(user);
            }
            catch(Exception  t)
            {
                logger.error(t + Arrays.asList(t.getStackTrace())
                        .stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"))
                );
            }
        }

    }

    @AfterEach
    void tearDown(TestInfo info)
    {
        if(info.getDisplayName().equals("loginUserIntegrationSuccessTest()"))
        {
            EntityTransaction entityTransaction = null;
            try(EntityManager em = entityManagerFactory.createEntityManager())
            {
                entityTransaction = em.getTransaction();
                entityTransaction.begin();

                String queryStr = "SELECT id FROM giannis.registered_users WHERE username='"+loginUser.getUsername()+"'";
                Object registeredUserId = em.createNativeQuery(queryStr ).getSingleResult();
                RegisteredUsersEntity registeredUsersEntity = em.find(RegisteredUsersEntity.class,registeredUserId);

                String queryStr2 = "SELECT id FROM giannis.authenticated_users WHERE username='"+loginUser.getUsername()+"'";
                Object authenticatedUserId = em.createNativeQuery(queryStr2 ).getSingleResult();
                AuthenticatedUsersEntity authenticatedUsersEntity = em.find(AuthenticatedUsersEntity.class,authenticatedUserId);

                em.remove(registeredUsersEntity);
                em.flush();
                em.remove(authenticatedUsersEntity);
                em.flush();
                entityTransaction.commit();
            }
            catch (Exception  e)
            {
                if ( entityTransaction != null && entityTransaction.isActive())
                {
                    entityTransaction.rollback();
                }
            }
        }
        else if(info.getDisplayName().equals("loginUserIntegrationFailureTest()"))
        {
            EntityTransaction entityTransaction = null;
            try(EntityManager em = entityManagerFactory.createEntityManager())
            {
                entityTransaction = em.getTransaction();
                entityTransaction.begin();

                String queryStr = "SELECT id FROM giannis.registered_users WHERE username='"+loginUser.getUsername()+"'";
                Object registeredUserId = em.createNativeQuery(queryStr ).getSingleResult();
                RegisteredUsersEntity registeredUsersEntity = em.find(RegisteredUsersEntity.class,registeredUserId);

                em.remove(registeredUsersEntity);
                em.flush();
                entityTransaction.commit();
            }
            catch (Exception  e)
            {
                if ( entityTransaction != null && entityTransaction.isActive())
                {
                    entityTransaction.rollback();
                }
            }
        }

    }


    @Test
    void loginUserIntegrationSuccessTest()
    {
        LoginResponse loginResponse = userLoginDbAdapter.loginUser(loginUser);
        try(EntityManager em = userLoginDbAdapter.getEntityManagerFactory().createEntityManager())
        {
            String queryStr = "SELECT id FROM giannis.authenticated_users WHERE username='"+loginUser.getUsername()+"'";
            Object authenticatedUserId = em.createNativeQuery(queryStr ).getSingleResult();
            AuthenticatedUsersEntity authenticatedUsersEntity = em.find(AuthenticatedUsersEntity.class,authenticatedUserId);

            assertEquals(loginUser.getUsername(), authenticatedUsersEntity.getUsername());
            assertEquals(0, authenticatedUsersEntity.getVersion());
            assertNotNull(loginResponse);
            assertEquals(loginResponse.getUsername(),loginUser.getUsername());
        }
    }

    @Test
    void loginUserIntegrationFailureTest()
    {
        LoginResponse loginResponse = userLoginDbAdapter.loginUser(loginUser);
        try(EntityManager em = userLoginDbAdapter.getEntityManagerFactory().createEntityManager())
        {
            Exception  Exception  = assertThrows(Exception .class, () -> {
                String queryStr = "SELECT id FROM giannis.authenticated_users WHERE username='"+loginUser.getUsername()+"'";
                em.createNativeQuery(queryStr ).getSingleResult();
            });
            String expectedMessage = "No result found for query";
            assertTrue(Exception .getMessage().contains(expectedMessage));
        }
        assertNull(loginResponse);
    }
}