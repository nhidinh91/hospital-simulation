package com.simulator.hospital.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimuView extends Application {

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