package com.simulator.hospital.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimuView extends Application {

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

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/simulator/hospital/mainMenu.fxml")); //load main Menu first

            //Set up stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Hospital System");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
