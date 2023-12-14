package ai.datawise.textbasedsocialmedia.app.utils;

import ai.datawise.textbasedsocialmedia.app.usercases.adapters.in.web.FollowerController;
import ai.datawise.textbasedsocialmedia.appconfig.ConfigInstances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class DbUtils
{
    @Getter
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DbUtils.class);

    @FunctionalInterface
    public interface EntityManagerFunction<T> {
        T apply(EntityManager entityManager) throws Exception;
    }

    public static <T> T inTransaction(EntityManagerFunction<T> function) throws Exception
    {
        EntityTransaction trans = null;
        try(EntityManager entityManager = ConfigInstances.getEntityManagerFactory().createEntityManager())
        {
            trans = entityManager.getTransaction();
            trans.begin();
            entityManagerThreadLocal.set(entityManager);
            T res = function.apply(entityManager);
            trans.commit();
            return res;
        }
        catch (Exception t)
        {
            logger.error(t + Arrays.asList(t.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            if (trans != null && trans.isActive()) {
                trans.rollback();
            }
            throw t;
        }
    }
}
