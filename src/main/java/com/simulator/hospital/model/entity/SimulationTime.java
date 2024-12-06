package com.simulator.hospital.model.entity;

import jakarta.persistence.*;

/**
 * Entity class representing the simulation time in the hospital simulation.
 * This class maps to the `simulation_time` table in the database.
 */
@Entity
@Table(name = "simulation_time")
public class SimulationTime {

    /**
     * Unique identifier for the simulation time record.
     * This field is automatically generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The simulation time value in seconds.
     */
    @Column
    private double time;

    /**
     * Constructor to initialize the simulation time with a specific value.
     *
     * @param time the simulation time value in seconds.
     */
    public SimulationTime(double time) {
        this.time = time;
    }

    /**
     * Default constructor.
     * Required for JPA.
     */
    public SimulationTime() {
    }

    /**
     * Gets the simulation time value.
     *
     * @return the simulation time value in seconds.
     */
    public double getTime() {
        return this.time;
    }

    /**
     * Sets the simulation time value.
     *
     * @param time the new simulation time value in seconds.
     */
    public void setTime(double time) {
        this.time = time;
    }
}
