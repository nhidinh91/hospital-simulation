package com.simulator.hospital.model.entity;

import jakarta.persistence.*;

@Entity
@Table( name = "simulation_time")

public class SimulationTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private double time;

    public SimulationTime(double time) {
        this.time = time;
    }

    public SimulationTime(){

    }

    public double getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
