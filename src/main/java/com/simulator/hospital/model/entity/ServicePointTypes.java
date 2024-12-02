package com.simulator.hospital.model.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "servicepointtypes")

public class ServicePointTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column (name = "type_name")
    private String typeName;
    @Column (name = "number_of_points")
    private int numberPoints;

    public ServicePointTypes(String typeName, int numberPoints) {
        this.typeName = typeName;
        this.numberPoints = numberPoints;
    }

    public ServicePointTypes() {

    }

    public int getId() {
        return id;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getNumberPoints() {
        return this.numberPoints;
    }

    public void setNumberPoints(int numberPoints) {
        this.numberPoints = numberPoints;
    }
}
