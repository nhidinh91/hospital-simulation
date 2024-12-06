package com.simulator.hospital.model.entity;

import jakarta.persistence.*;

/**
 * Entity class representing service point types in the hospital simulation.
 * This class maps to the `servicepointtypes` table in the database.
 */
@Entity
@Table(name = "servicepointtypes")
public class ServicePointTypes {

    /**
     * Unique identifier for the service point type.
     * This field is automatically generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The name of the service point type.
     */
    @Column(name = "type_name")
    private String typeName;

    /**
     * The number of points associated with the service point type.
     */
    @Column(name = "number_of_points")
    private int numberPoints;

    /**
     * Constructor to initialize the service point type with specific details.
     *
     * @param typeName     the name of the service point type.
     * @param numberPoints the number of points for the service point type.
     */
    public ServicePointTypes(String typeName, int numberPoints) {
        this.typeName = typeName;
        this.numberPoints = numberPoints;
    }

    /**
     * Default constructor.
     * Required for JPA.
     */
    public ServicePointTypes() {
    }

    /**
     * Gets the unique identifier of the service point type.
     *
     * @return the ID of the service point type.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the service point type.
     *
     * @return the service point type name.
     */
    public String getTypeName() {
        return this.typeName;
    }

    /**
     * Gets the number of points associated with the service point type.
     *
     * @return the number of points.
     */
    public int getNumberPoints() {
        return this.numberPoints;
    }

    /**
     * Sets the number of points for the service point type.
     *
     * @param numberPoints the new number of points.
     */
    public void setNumberPoints(int numberPoints) {
        this.numberPoints = numberPoints;
    }
}
