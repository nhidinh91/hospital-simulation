package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.datasource.MariaDbJpaConnection;
import com.simulator.hospital.model.entity.DelayTime;
import jakarta.persistence.EntityManager;

import java.util.List;

public class DelayTimeDao {
    // method to fetch the simulation time
    public List<DelayTime> getDelayTime() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select t from DelayTime t").getResultList();
    }

    // methods to persists or update simulation time
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
