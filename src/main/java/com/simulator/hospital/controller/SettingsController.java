package com.simulator.hospital.controller;

import com.simulator.hospital.model.dao.DelayTimeDao;
import com.simulator.hospital.model.dao.IntervalsDao;
import com.simulator.hospital.model.dao.ServicePointTypesDao;
import com.simulator.hospital.model.dao.SimulationTimeDao;
import com.simulator.hospital.model.entity.DelayTime;
import com.simulator.hospital.model.entity.Intervals;
import com.simulator.hospital.model.entity.ServicePointTypes;
import com.simulator.hospital.model.entity.SimulationTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsController {

    private final DelayTimeDao delayTimeDao = new DelayTimeDao();
    private final IntervalsDao intervalsDao = new IntervalsDao();
    private final ServicePointTypesDao spTypesDao = new ServicePointTypesDao();
    private final SimulationTimeDao simuTimeDao = new SimulationTimeDao();
    private boolean isEmpty = true;

    //method to load saved settings
    public Map<String, Object> loadSettings() {
        Map<String, Object> settings = new HashMap<>();

        //load Delay Time
        List<DelayTime> delayTimes = delayTimeDao.getDelayTime();
        if (!delayTimes.isEmpty()) {
            settings.put("DelayTime", delayTimes.get(0).getTime() / 1000); //convert to second
            isEmpty = false;
        }

        //load Intervals
        List<Intervals> intervals = intervalsDao.getAllIntervals();
        if (!intervals.isEmpty()) {
            for (Intervals interval : intervals) {
                settings.put(interval.getCategory(), interval.getTime()); //add intervals to settings with format <category>,<time>. exp: "ArrivalTime",10.
            }
            isEmpty = false;
        }

        //load Number of Service Points
        List<ServicePointTypes> servicePointTypes = spTypesDao.getAllServicePointTypes();
        if (!servicePointTypes.isEmpty()) {
            for (ServicePointTypes type : servicePointTypes) {
                settings.put(type.getTypeName(), type.getNumberPoints());
            }
            isEmpty = false;
        }

        //load Simulation Time
        List<SimulationTime> simulationTimes = simuTimeDao.getSimulationTime();
        if (!simulationTimes.isEmpty()) {
            settings.put("SimulationTime", simulationTimes.get(0).getTime());
            isEmpty = false;
        }

        return settings;
    }

    //method to save or update settings
    public void saveSettings(Map<String, Object> settings) {
        //save Delay Time
        long delayTime = (long) settings.get("DelayTime") * 1000; // Convert seconds to ms
        if (isEmpty) {
            delayTimeDao.persist(new DelayTime(delayTime));
        } else {
            delayTimeDao.update(new DelayTime(delayTime));
        }

        //save Intervals
        Intervals arrivalInterval = new Intervals("Arrival", "ArrivalTime", (double) settings.get("ArrivalTime"));
        Intervals registerInterval = new Intervals("Service", "RegisterTime", (double) settings.get("RegisterTime"));
        Intervals generalInterval = new Intervals("Service", "GeneralTime", (double) settings.get("GeneralTime"));
        Intervals specialistInterval = new Intervals("Service", "SpecialistTime", (double) settings.get("SpecialistTime"));

        if (isEmpty) {
            intervalsDao.persist(arrivalInterval);
            intervalsDao.persist(registerInterval);
            intervalsDao.persist(generalInterval);
            intervalsDao.persist(specialistInterval);
        } else {
            intervalsDao.update(arrivalInterval);
            intervalsDao.update(registerInterval);
            intervalsDao.update(generalInterval);
            intervalsDao.update(specialistInterval);
        }

        //save Number of Service Points
        ServicePointTypes registerType = new ServicePointTypes("Register", (int) settings.get("Register"));
        ServicePointTypes generalType = new ServicePointTypes("General", (int) settings.get("General"));
        ServicePointTypes specialistType = new ServicePointTypes("Specialist", (int) settings.get("Specialist"));

        if (isEmpty) {
            spTypesDao.persist(registerType);
            spTypesDao.persist(generalType);
            spTypesDao.persist(specialistType);
        } else {
            spTypesDao.update(registerType);
            spTypesDao.update(generalType);
            spTypesDao.update(specialistType);
        }

        //save Simulation Time
        SimulationTime simulationTime = new SimulationTime((double) settings.get("SimulationTime"));
        if (isEmpty) {
            simuTimeDao.persist(simulationTime);
        } else {
            simuTimeDao.update(simulationTime);
        }

        //set isEmpty to false after the first save operation
        isEmpty = false;
    }
}
