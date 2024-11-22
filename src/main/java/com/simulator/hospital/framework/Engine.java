package com.simulator.hospital.framework;

import com.simulator.hospital.model.Customer;
import com.simulator.hospital.model.ServicePoint;
import com.simulator.hospital.model.ServiceUnit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class Engine provides the core structure and logic for running a simulation.
 * It defines the main loop and manages the simulation time, clock, and event list.
 * Concrete subclasses must implement the abstract methods to define specific simulation behavior.
 */

public abstract class Engine {
	private double simulationTime = 0;	// time when the simulation will be stopped
	private Clock clock;				// to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
	protected EventList eventList;		// events to be processed are stored here

	public Engine() {
		clock = Clock.getInstance();
		
		eventList = new EventList();

	}

	public void setSimulationTime(double time) {	// define how long we will run the simulation
		simulationTime = time;
	}

	//Runs the main simulation loop, progressing through A-phase, B-phase, and C-phase until the simulation time is reached
	public void run(){
		initialize();		// Set up the initial state, e.g., generate the first event

		while (simulate()) {		// Continue simulation while within the time limit
			Trace.out(Trace.Level.INFO, "\nA-phase: time is " + currentTime());
			clock.setClock(currentTime());		// Continue simulation while within the time limit
			
			Trace.out(Trace.Level.INFO, "\nB-phase:" );
			runBEvents();		// Execute all B-events at the current time


			Trace.out(Trace.Level.INFO, "\nC-phase:" );
			tryCEvents();		// Attempt to execute all eligible C-events

		}

		results();		// Attempt to execute all eligible C-events
	}

	// Processes all B-events scheduled for the current time
	private void runBEvents() {
		while (eventList.getNextEventTime() == clock.getClock()){
			runEvent(eventList.remove());		// Execute and remove the event from the list
		}
	}

	// Get the time of the next event in the event list
	private double currentTime(){
		return eventList.getNextEventTime();
	}

	// Checks if the simulation should continue
	private boolean simulate(){
		return clock.getClock() < simulationTime;
	}

	protected abstract HashMap<Customer, ServiceUnit> runEvent(Event t);	// Defined in simu.model-package's class who is inheriting the Engine class

	protected abstract HashMap<Customer, ServicePoint> tryCEvents();		// Defined in simu.model-package's class who is inheriting the Engine class

	protected abstract void initialize(); 		// Defined in simu.model-package's class who is inheriting the Engine class

	protected abstract void results(); 			// Defined in simu.model-package's class who is inheriting the Engine class
}