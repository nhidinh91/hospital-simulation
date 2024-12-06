package com.simulator.hospital.controller;

import com.simulator.hospital.model.dao.*;
import com.simulator.hospital.model.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing the settings of the hospital simulation.
 */
public class SettingsController {

    private final DelayTimeDao delayTimeDao = new DelayTimeDao();
    private final IntervalsDao intervalsDao = new IntervalsDao();
    private final ServicePointTypesDao spTypesDao = new ServicePointTypesDao();
    private final SimulationTimeDao simuTimeDao = new SimulationTimeDao();

    /**
     * Loads the saved settings from the database.
     *
     * @return a map containing the settings with their respective values.
     */
    public Map<String, Object> loadSettings() {
        Map<String, Object> settings = new HashMap<>();

        //load Delay Time
        List<DelayTime> delayTimes = delayTimeDao.getDelayTime();
        if (!delayTimes.isEmpty()) {
            settings.put("DelayTime", delayTimes.get(0).getTime());
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

    /**
     * Saves or updates the settings in the database.
     *
     * @param settings a map containing the settings with their respective values.
     */
    public void saveSettings(Map<String, Object> settings) {
        //save Delay Time
        long delayTime = (long) settings.get("DelayTime");
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
