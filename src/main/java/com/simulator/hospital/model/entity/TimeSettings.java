package com.simulator.hospital.model.entity;

import jakarta.persistence.*;

@Entity
@Table( name = "timesettings")

public class TimeSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "simulation_time")
    private double simulationTime;
    @Column(name = "delay_time")
    private double delayTime;

    public TimeSettings(double simulationTime, double delayTime) {
        this.simulationTime = simulationTime;
        this.delayTime = delayTime;
    }

    public TimeSettings(){

    }

    public double getSimulationTime() {
        return this.simulationTime;
    }

    public double getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(double delayTime) {
        this.delayTime = delayTime;
    }

    public void setSimulationTime(double simulationTime) {
        this.simulationTime = simulationTime;
    }
}
