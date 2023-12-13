package ai.datawise.textbasedsocialmedia.app.utils;

import ai.datawise.textbasedsocialmedia.appconfig.ConfigInstances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.Getter;

public class DbUtils
{
    @Getter
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
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
            if (trans != null && trans.isActive()) {
                trans.rollback();
            }
            throw t;
        }
    }

}
