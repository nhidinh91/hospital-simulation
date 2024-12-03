package com.simulator.hospital.controller;

import com.simulator.hospital.model.dao.*;
import com.simulator.hospital.model.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsController {

    private final DelayTimeDao delayTimeDao = new DelayTimeDao();
    private final IntervalsDao intervalsDao = new IntervalsDao();
    private final ServicePointTypesDao spTypesDao = new ServicePointTypesDao();
    private final SimulationTimeDao simuTimeDao = new SimulationTimeDao();

    //method to load saved settings
    public Map<String, Object> loadSettings() {
        Map<String, Object> settings = new HashMap<>();

        //load Delay Time
        List<DelayTime> delayTimes = delayTimeDao.getDelayTime();
        if (!delayTimes.isEmpty()) {
            settings.put("DelayTime", delayTimes.get(0).getTime() / 1000); //convert ms to second
        }

        //load Intervals
        List<Intervals> intervals = intervalsDao.getAllIntervals();
        if (!intervals.isEmpty()) {
            for (Intervals interval : intervals) {
                settings.put(interval.getCategory(), interval.getTime()); //add intervals to settings with format <category>,<time>. exp: "ArrivalTime",10.
            }
        }

        //load Number of Service Points
        List<ServicePointTypes> servicePointTypes = spTypesDao.getAllServicePointTypes();
        if (!servicePointTypes.isEmpty()) {
            for (ServicePointTypes type : servicePointTypes) {
                settings.put(type.getTypeName(), type.getNumberPoints());
            }
        }

        //load Simulation Time
        List<SimulationTime> simulationTimes = simuTimeDao.getSimulationTime();
        if (!simulationTimes.isEmpty()) {
            settings.put("SimulationTime", simulationTimes.get(0).getTime());
        }

        return settings;
    }

    //method to save or update settings
    public void saveSettings(Map<String, Object> settings) {
        //save Delay Time
        long delayTime = (long) settings.get("DelayTime") * 1000; //convert secound seconds to ms
        delayTimeDao.persistOrUpdate(new DelayTime(delayTime));

        //save Intervals
        Intervals arrivalInterval = new Intervals("Arrival", "ArrivalTime", (double) settings.get("ArrivalTime"));
        Intervals registerInterval = new Intervals("Service", "RegisterTime", (double) settings.get("RegisterTime"));
        Intervals generalInterval = new Intervals("Service", "GeneralTime", (double) settings.get("GeneralTime"));
        Intervals specialistInterval = new Intervals("Service", "SpecialistTime", (double) settings.get("SpecialistTime"));

        intervalsDao.persistOrUpdate(arrivalInterval);
        intervalsDao.persistOrUpdate(registerInterval);
        intervalsDao.persistOrUpdate(generalInterval);
        intervalsDao.persistOrUpdate(specialistInterval);

        //save Number of Service Points
        ServicePointTypes registerType = new ServicePointTypes("Register", (int) settings.get("Register"));
        ServicePointTypes generalType = new ServicePointTypes("General", (int) settings.get("General"));
        ServicePointTypes specialistType = new ServicePointTypes("Specialist", (int) settings.get("Specialist"));

        spTypesDao.persistOrUpdate(registerType);
        spTypesDao.persistOrUpdate(generalType);
        spTypesDao.persistOrUpdate(specialistType);

        //save Simulation Time
        SimulationTime simulationTime = new SimulationTime((double) settings.get("SimulationTime"));
        simuTimeDao.persistOrUpdate(simulationTime);
    }
}
