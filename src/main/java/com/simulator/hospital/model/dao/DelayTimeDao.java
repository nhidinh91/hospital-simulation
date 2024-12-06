package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.datasource.MariaDbJpaConnection;
import com.simulator.hospital.model.entity.DelayTime;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link DelayTime} entities.
 * Provides methods to fetch, persist, or update simulation delay times
 * in the database.
 */
public class DelayTimeDao {

    /**
     * Retrieves the list of all delay times stored in the database.
     *
     * @return a {@link List} of {@link DelayTime} objects representing the delay times.
     */
    public List<DelayTime> getDelayTime() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select t from DelayTime t", DelayTime.class).getResultList();
    }

    /**
     * Persists a new delay time or updates an existing one in the database.
     * If a {@link DelayTime} entity with ID 1 exists, it will be updated with
     * the provided time. Otherwise, the provided {@link DelayTime} will be persisted as a new entity.
     *
     * @param delayTime the {@link DelayTime} entity containing the time to be saved or updated.
     */
    public void persistOrUpdate(DelayTime delayTime) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        DelayTime existingDelayTime = em.find(DelayTime.class, 1);
        if (existingDelayTime != null) {
            existingDelayTime.setTime(delayTime.getTime());
            em.merge(existingDelayTime);
        } else {
            em.persist(delayTime);
        }
        em.getTransaction().commit();
    }
}
