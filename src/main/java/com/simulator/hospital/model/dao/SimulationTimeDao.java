package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.datasource.MariaDbJpaConnection;
import com.simulator.hospital.model.entity.SimulationTime;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link SimulationTime} entities.
 * Provides methods to fetch, persist, or update simulation time in the database.
 */
public class SimulationTimeDao {

    /**
     * Retrieves all simulation time records from the database.
     *
     * @return a {@link List} of {@link SimulationTime} objects representing the simulation times.
     */
    public List<SimulationTime> getSimulationTime() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select t from SimulationTime t", SimulationTime.class).getResultList();
    }

    /**
     * Persists a new simulation time or updates an existing one in the database.
     * If a {@link SimulationTime} entity with ID 1 exists, it will be updated with
     * the provided time. Otherwise, the provided {@link SimulationTime} will be persisted as a new entity.
     *
     * @param simulationTime the {@link SimulationTime} entity containing the time to be saved or updated.
     */
    public void persistOrUpdate(SimulationTime simulationTime) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();

        // Check if a simulation time with ID 1 exists
        SimulationTime existingSimulationTime = em.find(SimulationTime.class, 1);
        if (existingSimulationTime != null) {
            // Update the existing simulation time
            existingSimulationTime.setTime(simulationTime.getTime());
            em.merge(existingSimulationTime);
        } else {
            // Persist the new simulation time
            em.persist(simulationTime);
        }

        em.getTransaction().commit();
    }
}
