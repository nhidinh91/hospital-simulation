package com.simulator.hospital.model.entity;

import jakarta.persistence.*;

/**
 * Entity class representing the delay time in the hospital simulation.
 * This class maps to the `delay_time` table in the database.
 */
@Entity
@Table(name = "delay_time")
public class DelayTime {

    /**
     * Unique identifier for the delay time record.
     * This field is automatically generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The delay time value in milliseconds.
     */
    @Column
    private long time;

    /**
     * Default constructor.
     * Required for JPA.
     */
    public DelayTime() {
    }

    /**
     * Constructor to initialize the delay time.
     *
     * @param time the delay time value in milliseconds.
     */
    public DelayTime(long time) {
        this.time = time;
    }

    /**
     * Gets the delay time value.
     *
     * @return the delay time value in milliseconds.
     */
    public long getTime() {
        return this.time;
    }

    /**
     * Sets the delay time value.
     *
     * @param time the delay time value in milliseconds.
     */
    public void setTime(long time) {
        this.time = time;
    }
}
