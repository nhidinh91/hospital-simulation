package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.entity.*;
import com.simulator.hospital.model.datasource.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TimeSettingsDao {

    // method to insert a new interval
    public void persist(TimeSettings timeSettings) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.persist(timeSettings);
        em.getTransaction().commit();
    }

    // method to fetch all time settings (simulation time and delay time)
    public List<TimeSettings> getAllTimeSettings() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("select t from TimeSettings t").getResultList();
    }

    // method to update time settings
    public void update(TimeSettings timeSettings) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.merge((timeSettings));
        em.getTransaction().commit();
    }
}
