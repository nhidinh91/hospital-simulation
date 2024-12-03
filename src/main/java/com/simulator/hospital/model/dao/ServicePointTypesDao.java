package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.entity.*;
import com.simulator.hospital.model.datasource.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ServicePointTypesDao {

    // method to fetch all service point types in the servicepointtypes table
    public List<ServicePointTypes> getAllServicePointTypes() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        List<ServicePointTypes> servicePointTypes = em.createQuery("select s from ServicePointTypes s").getResultList();
        return servicePointTypes;
    }

    // method to persist or update a service point type based on type name
    public void persistOrUpdate(ServicePointTypes servicePointType) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();

        //check if this service point type exist in database
        List<ServicePointTypes> existingServicePointTypes = em.createQuery("select s from ServicePointTypes s where s.typeName = :typeName", ServicePointTypes.class)
                .setParameter("typeName", servicePointType.getTypeName())
                .getResultList();
        if (existingServicePointTypes.isEmpty()) {
            em.persist(servicePointType); //persist if service point type not exist
        } else {
            ServicePointTypes existingServicePointType = existingServicePointTypes.get(0); //get first and only result
            existingServicePointType.setNumberPoints(servicePointType.getNumberPoints()); //update number
            em.merge(existingServicePointType); //merge with existing result
        }
        em.getTransaction().commit();
    }
}
