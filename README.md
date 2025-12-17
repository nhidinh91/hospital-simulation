# Hospital System Simulator
*An Object-Oriented Discrete-Event Simulation Project*

## Overview
The **Hospital System Simulator** is a Java-based **Object-Oriented Programming (OOP)** project that models and analyzes patient flow within a hospital environment. The simulation replicates real-world hospital workflows such as patient arrivals, queue management, registration, general examinations, specialist examinations, and exits.

The system is designed as an **educational and analytical tool**, enabling users to experiment with different hospital configurations, observe bottlenecks, and evaluate resource utilization through both real-time visualization and post-simulation statistics.

---

## Project Goals
- Model hospital workflows using **discrete-event simulation**
- Apply **OOP principles** and clean architecture
- Visualize patient flow with a **JavaFX GUI**
- Identify bottlenecks and inefficiencies
- Analyze key performance metrics such as waiting times and utilization

---

## Key Features
- Patient arrival simulation using exponential distributions
- Registration, general exam, and specialist exam service points
- Queue management with infinite-capacity queues
- Real-time animated visualization of patient flow
- Post-simulation performance metrics and charts
- Configurable simulation parameters
- Persistent storage of simulation settings using MariaDB
- MVC-based, modular, and extensible design

---

## Simulation Concepts
The simulator is built around core queueing and simulation concepts:

- **Customer (Patient)**: Represents an individual entering the hospital system
- **Service Point**: A unit that serves patients (e.g. registration desk)
- **Queue**: Waiting line for a service point
- **Event**: A scheduled action (arrival, service start, service completion)
- **Event List**: Priority queue managing simulation events chronologically
- **Clock**: Global simulation time reference

The simulation follows a **three-phase approach (ABC)**:
- **A-phase**: Advance the simulation clock
- **B-phase**: Process scheduled events
- **C-phase**: Trigger conditional events (start service if possible)

---

## Architecture
The project follows an extended **Model–View–Controller (MVC)** architecture.

### Model
Handles data, simulation logic, and persistence:
- `entity` – ORM-mapped database entities
- `dao` – Data access layer
- `datasource` – Database connection management
- `logic` – Core simulation logic (events, service units, customers)

### View
- JavaFX-based GUI
- Main Menu View
- Simulation View (real-time animation)
- Result View (charts & statistics)

### Controller
- Coordinates interactions between Model and View
- Handles simulation execution and UI updates

### Framework
- Simulation clock
- Event scheduler
- Utility and helper classes

---

## User Interface

### Main Menu
- Configure number of service points
- Set arrival intervals and service times
- Define simulation duration and speed

### Simulation View
- Animated patient flow
- Real-time queues and service points
- Adjustable simulation speed

### Result View
- Average waiting time
- Service point utilization
- Total customers served
- Charts for performance comparison

---

## Performance Metrics
The simulator calculates:
- Average patient waiting time
- Total and mean service time per service point
- Number of customers served
- Utilization rate of each service point

These metrics help identify:
- Bottlenecks
- Underutilized resources
- Effects of workload imbalance

---

## Database Integration
The project uses **MariaDB** with **JPA (Jakarta Persistence API)**.

Stored data includes:
- Number of service points
- Arrival and service time parameters
- Simulation duration
- Delay time (simulation speed)

This allows simulation configurations to be reused across runs.

---

## Testing

### Unit Testing
- Implemented with **JUnit**
- Focuses on entity and configuration classes
- Validates edge cases and data consistency

### Validation Testing
- Comparison with queueing theory expectations
- Repeated runs using fixed random seeds
- Verification of consistency and correctness

---

## Technologies Used
- **Java** (OOP, multithreading)
- **JavaFX** (GUI, animations, charts)
- **MariaDB** (configuration persistence)
- **JPA / Hibernate** (ORM)
- **EDUNI Distributions Library** (random number generation)
- **JUnit** (testing)


