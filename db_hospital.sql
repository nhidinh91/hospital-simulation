- Drop the database if it already exists
DROP DATABASE IF EXISTS hospital_simulation;

-- Create the database
CREATE DATABASE hospital_simulation;

-- Use the database
USE hospital_simulation;

-- Create the ServicePointTypes
CREATE TABLE ServicePointTypes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(255) NOT NULL,
    number_of_points INT NOT NULL CHECK (number_of_points >= 0)
);
-- Create the Intervals table
CREATE TABLE Intervals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- 'arrival' or 'service'
    category VARCHAR(255) NULL, -- 'Registration', 'General Health Exam', etc., or NULL for global settings
    time DOUBLE NOT NULL CHECK (time >= 0)
);
-- Table: time_settings
CREATE TABLE TimeSettings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    simulation_time DOUBLE NOT NULL CHECK (simulation_time >= 0),
    delay_time DOUBLE NOT NULL CHECK (delay_time >= 0)
);