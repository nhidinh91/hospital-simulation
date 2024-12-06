package com.simulator.hospital.model.entity;

import jakarta.persistence.*;

/**
 * Entity class representing intervals used in the hospital simulation.
 * This class maps to the `intervals` table in the database.
 */
@Entity
@Table(name = "intervals")
public class Intervals {

    /**
     * Unique identifier for the interval record.
     * This field is automatically generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The type of interval (e.g., service, delay, etc.).
     */
    @Column
    private String type;

    /**
     * The category of the interval (e.g., specific to certain tasks or entities).
     */
    private String category;

    /**
     * The time value associated with the interval.
     */
    private double time;

    /**
     * Default constructor.
     * Required for JPA.
     */
    public Intervals() {
        // empty constructor
    }

    /**
     * Constructor to initialize the interval with specific details.
     *
     * @param type     the type of interval.
     * @param category the category of interval.
     * @param time     the time value for the interval.
     */
    public Intervals(String type, String category, double time) {
        this.type = type;
        this.category = category;
        this.time = time;
    }

    /**
     * Gets the unique identifier of the interval.
     *
     * @return the interval ID.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the type of the interval.
     *
     * @return the interval type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the category of the interval.
     *
     * @return the interval category.
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Gets the time value of the interval.
     *
     * @return the interval time value.
     */
    public double getTime() {
        return this.time;
    }

    /**
     * Sets the time value of the interval.
     *
     * @param time the new time value for the interval.
     */
    public void setTime(double time) {
        this.time = time;
    }
}
