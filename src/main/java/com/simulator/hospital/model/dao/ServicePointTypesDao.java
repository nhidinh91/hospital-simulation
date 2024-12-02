package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.entity.*;
import com.simulator.hospital.model.datasource.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ServicePointTypesDao {

    // method to insert a new service point type
    public void persist(ServicePointTypes servicePointType) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.persist(servicePointType);
        em.getTransaction().commit();
    }

    // method to fetch all service point types in the servicepointtypes table
    public List<ServicePointTypes> getAllServicePointTypes() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        List<ServicePointTypes> servicePointTypes = em.createQuery("select s from ServicePointTypes s").getResultList();
        return servicePointTypes;
    }

    // method to fetch the number of points for a specified type name
    public List<Double> getNumberByTypename(String typeName) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select s.numberPoints from ServicePointTypes s where s.typeName = :typeName")
                .setParameter("typeName", typeName)
                .getResultList();
    }

    // method to update an interval
    public void update(ServicePointTypes servicePointType) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.merge(servicePointType);
        em.getTransaction().commit();
    }
}
