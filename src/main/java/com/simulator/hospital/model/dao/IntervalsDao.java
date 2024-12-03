package com.simulator.hospital.model.dao;

import com.simulator.hospital.model.entity.*;
import com.simulator.hospital.model.datasource.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class IntervalsDao {

    // method to fetch all intervals in the intervals table
    public List<Intervals> getAllIntervals() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        List<Intervals> intervals = em.createQuery("select i from Intervals i").getResultList();
        return intervals;
    }

    // method to persist or update an interval based on category
    public void persistOrUpdate(Intervals interval) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        //check if interval's category exist in database
        List<Intervals> existingIntervals = em.createQuery("select i from Intervals i where i.category = :category", Intervals.class)
                .setParameter("category", interval.getCategory())
                .getResultList();
        if (existingIntervals.isEmpty()) {
            em.persist(interval); //persist if category not exist
        } else {
            Intervals existingInterval = existingIntervals.get(0); //get first and only result
            existingInterval.setTime(interval.getTime()); //update time
            em.merge(existingInterval); //merge with existing result
        }
        em.getTransaction().commit();
    }
}
