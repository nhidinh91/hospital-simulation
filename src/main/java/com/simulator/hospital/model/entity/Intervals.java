package com.simulator.hospital.model.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "intervals")

public class Intervals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String type;
    private String category;
    private double time;

    public Intervals() {
        // Default constructor
    }

    public Intervals(String type, String category, double time) {
        this.type = type;
        this.category = category;
        this.time = time;
    }

    public int getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getCategory() {
        return this.category;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
