package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.entity.ServicePointTypes;
import com.simulator.hospital.model.datasource.MariaDbJpaConnection;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link ServicePointTypes} entities.
 * Provides methods to fetch, persist, or update service point types in the database.
 */
public class ServicePointTypesDao {

    /**
     * Retrieves all service point types from the database.
     *
     * @return a {@link List} of {@link ServicePointTypes} objects representing all service point types.
     */
    public List<ServicePointTypes> getAllServicePointTypes() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select s from ServicePointTypes s", ServicePointTypes.class).getResultList();
    }

    /**
     * Persists a new service point type or updates an existing one in the database based on the type name.
     * If a service point type with the same type name already exists, its number of points will be updated.
     * Otherwise, the provided service point type will be persisted as a new entity.
     *
     * @param servicePointType the {@link ServicePointTypes} entity to be saved or updated.
     */
    public void persistOrUpdate(ServicePointTypes servicePointType) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();

        // Check if a service point type with the same type name already exists
        List<ServicePointTypes> existingServicePointTypes = em.createQuery("select s from ServicePointTypes s where s.typeName = :typeName", ServicePointTypes.class)
                .setParameter("typeName", servicePointType.getTypeName())
                .getResultList();

        if (existingServicePointTypes.isEmpty()) {
            // Persist the new service point type if it does not exist
            em.persist(servicePointType);
        } else {
            // Update the number of points for the existing service point type
            ServicePointTypes existingServicePointType = existingServicePointTypes.get(0);
            existingServicePointType.setNumberPoints(servicePointType.getNumberPoints());
            em.merge(existingServicePointType);
        }

        em.getTransaction().commit();
    }
}
