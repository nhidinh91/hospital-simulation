package com.simulator.hospital.model.logic;

import com.simulator.hospital.framework.IEventType;

/**
 * Enumeration representing the types of events in the hospital simulation.
 * These events are defined by the requirements of the simulation model.
 * Implements the {@link IEventType} interface to integrate with the simulation framework.
 */
public enum EventType implements IEventType {
	/**
	 * Represents the arrival event at the first service point.
	 */
	ARR1,

	/**
	 * Represents the departure event from the first service point.
	 */
	DEP1,

	/**
	 * Represents the departure event from the second service point.
	 */
	DEP2,

	/**
	 * Represents the departure event from the third service point.
	 */
	DEP3;
}
