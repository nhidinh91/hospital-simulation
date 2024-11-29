package com.simulator.hospital.framework;

/**
 * Singleton class that represents the simulation clock, used to keep track of the current time.
 * Only one instance of the Clock can exist to maintain a consistent simulation time.
 */

public class Clock {
	private double clock;
	private static Clock instance;
	
	private Clock(){
		clock = 0;
	}
	
	public static Clock getInstance(){
		if (instance == null){
			instance = new Clock();
		}
		return instance;
	}
	
	public void setClock(double clock){
		this.clock = clock;
	}

	public double getClock(){
		return clock;
	}

	public void reset() {
		clock = 0;
	}
}
