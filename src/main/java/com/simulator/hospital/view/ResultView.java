package com.simulator.hospital.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ResultView extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/result.fxml"));
        Parent root = loader.load(); // Load FXML and get the root node

        // Create and set up the scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Result View");
        primaryStage.show();
    }
}
