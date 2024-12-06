package com.simulator.hospital.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main view class for the hospital simulation application.
 * This class is responsible for launching the main menu view.
 */
public class SimuView extends Application {

    /**
     * Starts the JavaFX application by loading the main menu view.
     *
     * @param primaryStage the primary stage for this application
     */
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