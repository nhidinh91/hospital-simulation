package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.entity.Intervals;
import com.simulator.hospital.model.datasource.MariaDbJpaConnection;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link Intervals} entities.
 * Provides methods to fetch, persist, or update intervals in the database.
 */
public class IntervalsDao {

    /**
     * Retrieves all interval records from the database.
     *
     * @return a {@link List} of {@link Intervals} objects representing all intervals.
     */
    public List<Intervals> getAllIntervals() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select i from Intervals i", Intervals.class).getResultList();
    }

    /**
     * Persists a new interval or updates an existing one in the database based on the category.
     * If an interval with the same category already exists, its time will be updated. Otherwise,
     * the provided interval will be persisted as a new entity.
     *
     * @param interval the {@link Intervals} entity to be saved or updated.
     */
    public void persistOrUpdate(Intervals interval) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();

        // Check if an interval with the same category already exists
        List<Intervals> existingIntervals = em.createQuery("select i from Intervals i where i.category = :category", Intervals.class)
                .setParameter("category", interval.getCategory())
                .getResultList();

        if (existingIntervals.isEmpty()) {
            // Persist the new interval if the category does not exist
            em.persist(interval);
        } else {
            // Update the time of the existing interval
            Intervals existingInterval = existingIntervals.get(0);
            existingInterval.setTime(interval.getTime());
            em.merge(existingInterval);
        }

        em.getTransaction().commit();
    }
}
