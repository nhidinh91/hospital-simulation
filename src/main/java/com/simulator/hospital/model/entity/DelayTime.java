package com.simulator.hospital.model.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "delay_time")

public class DelayTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private long time;

    public DelayTime() {

    }

    public DelayTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
