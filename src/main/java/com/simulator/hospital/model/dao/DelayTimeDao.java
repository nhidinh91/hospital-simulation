package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.datasource.MariaDbJpaConnection;
import com.simulator.hospital.model.entity.DelayTime;
import jakarta.persistence.EntityManager;

import java.util.List;

public class DelayTimeDao {

    // method to insert a new simulation time
    public void persist(DelayTime delayTime) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.persist(delayTime);
        em.getTransaction().commit();
    }

    // method to fetch the simulation time
    public List<DelayTime> getSimulationTime() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select t from DelayTime t").getResultList();
    }

    // methods to update simulation time
    public void update(DelayTime delayTime) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.merge(delayTime);
        em.getTransaction().commit();
    }
}
