package com.simulator.hospital.model.datasource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class for managing the JPA connection to a MariaDB database.
 * This class provides a singleton instance of {@link EntityManager} for database interactions.
 */
public class MariaDbJpaConnection {

    // Singleton instance of the EntityManagerFactory
    private static EntityManagerFactory emf = null;

    // Singleton instance of the EntityManager
    private static EntityManager em = null;

    /**
     * Provides a singleton instance of {@link EntityManager}.
     * Initializes the {@link EntityManagerFactory} and {@link EntityManager} if not already created.
     *
     * @return an {@link EntityManager} instance for interacting with the MariaDB database.
     */
    public static EntityManager getInstance() {
        if (em == null) {
            if (emf == null) {
                // Create the EntityManagerFactory using the persistence unit name
                emf = Persistence.createEntityManagerFactory("HospitalSimulationMariaDbUnit");
            }
            // Create the EntityManager from the EntityManagerFactory
            em = emf.createEntityManager();
        }
        return em;
    }
}
