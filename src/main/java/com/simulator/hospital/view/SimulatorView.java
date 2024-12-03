package com.simulator.hospital.view;
import com.simulator.hospital.framework.Trace;
//import com.simulator.hospital.framework.Trace.Level;

import com.simulator.hospital.controller.SimulatorController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimulatorView extends Application {
    private ChoiceBox<Integer> numberRegister;
    private ChoiceBox<Integer> numberGeneral;
    private ChoiceBox<Integer> numberSpecialist;
    private TextField registerTime;
    private TextField generalTime;
    private TextField specialistTime;
    private TextField arrivalTime;
    private TextField simulationTime;
    private TextField delayTime;
    private Button startButton;
    private SimulatorController controller;

    @Override
    public void init() {
        this.controller = new SimulatorController(this);
        this.numberRegister = new ChoiceBox<>();
        this.numberRegister.getItems().addAll(1, 2, 3);
        this.numberGeneral = new ChoiceBox<>();
        this.numberGeneral.getItems().addAll(1, 2, 3);
        this.numberSpecialist = new ChoiceBox<>();
        this.numberSpecialist.getItems().addAll(1, 2, 3);
        this.registerTime = new TextField("Register Time");
        this.generalTime = new TextField("General Time");
        this.specialistTime = new TextField("Specialist Time");
        this.arrivalTime = new TextField("Arrival Time");
        this.simulationTime = new TextField("Simulation Time");
        this.delayTime = new TextField("Delay time");
        this.startButton = new Button("Start");
    }

    public int getNumberRegister() {
        return this.numberRegister.getValue();
    }

    public int getNumberGeneral() {
        return this.numberGeneral.getValue();
    }

    public int getNumberSpecialist() {
        return this.numberSpecialist.getValue();
    }

    public double getRegisterTime() {
        return Double.parseDouble(this.registerTime.getText());
    }

    public double getGeneralTime() {
        return Double.parseDouble(this.generalTime.getText());
    }

    public double getSpecialistTime() {
        return Double.parseDouble(this.specialistTime.getText());
    }

    public double getArrivalTime() {
        return Double.parseDouble(this.arrivalTime.getText());
    }

    public double getSimulationTime() {
        return Double.parseDouble(this.simulationTime.getText());
    }

    public long getDelayTime() {
        return Long.parseLong(this.delayTime.getText());
    }

    public void displayClock(double time) {
        System.out.printf("Clock is at: %.2f\n", time);
    }

    public void displayBEvent(int customerId, int serviceUnitNumber){
        if (serviceUnitNumber != 0) {
            System.out.printf("Customer %d move to queue of Service Unit %d\n", customerId, serviceUnitNumber);
        } else {
            System.out.printf("Customer %d completed service, is removed from system\n", customerId);
        }
    }

    public void displayCEvent(int customerId, int servicePointId) {
        System.out.printf("Customer %d is being served at service point %d\n", customerId, servicePointId);
    }

    public void start(Stage stage) {
        stage.setTitle("Hospital Simulator");
        TilePane pane = new TilePane();

        // get interval values from database
        HashMap<Integer, Double> intervals = controller.getIntervals();
        for (Map.Entry<Integer, Double> entry : intervals.entrySet()) {
            switch (entry.getKey()){
                case 1:
                    arrivalTime.setText(entry.getValue().toString());
                    break;
                case 2:
                    registerTime.setText(entry.getValue().toString());
                    break;
                case 3:
                    generalTime.setText(entry.getValue().toString());
                    break;
                case 4:
                    specialistTime.setText(entry.getValue().toString());
            }
        }

        // get number of points from database
        HashMap<Integer, Integer> numberPoints = controller.getNumberOfPoints();
        for (Map.Entry<Integer, Integer> entry : numberPoints.entrySet()) {
            switch (entry.getKey()) {
                case 1:
                    numberRegister.setValue(entry.getValue());
                    break;
                case 2:
                    numberGeneral.setValue(entry.getValue());
                    break;
                case 3:
                    numberSpecialist.setValue(entry.getValue());
            }
        }

        // get the simulation time from database
        simulationTime.setText(Double.toString(controller.getSimulationTimeDb()));

        // get the delay time from the database
        delayTime.setText(Long.toString(controller.getDelayTimeDb()));

        pane.getChildren().addAll(
                numberRegister, numberGeneral, numberSpecialist,
                registerTime, generalTime, specialistTime,
                arrivalTime, delayTime, simulationTime, startButton
        );
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();

        final boolean[] activated = {false}; // Use an array to wrap the boolean
        final Thread[] simulator = {null};   // Use an array for the simulator thread

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                controller.initializeModel();
                simulator[0] = new Thread(controller);
                simulator[0].start();
                activated[0] = true; // Modify the wrapped boolean
            }
        });

        // Monitor the activation status in a new thread
        new Thread(() -> {
            while (!activated[0]) {
                try {
                    Thread.sleep(100); // Wait until activated
                } catch (InterruptedException e) {
                    System.err.println("Waiting thread interrupted");
                }
            }

            // During simulation, if the user changes the value of delay time
            while (simulator[0] != null && simulator[0].isAlive()) {
                long delay = getDelayTime();
                controller.setDelayTime(delay);
                try {
                    Thread.sleep(100); // Avoid constant polling
                } catch (InterruptedException e) {
                    System.err.println("Delay adjustment thread interrupted");
                }
            }
        }).start();
    }
}
