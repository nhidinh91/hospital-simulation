package com.simulator.hospital.controller;

import com.simulator.hospital.model.SimulatorModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    @FXML
    public TextField registerTime, generalTime, specialistTime, arrivalTime, simulationTimeField;
    @FXML
    private ChoiceBox<String> registerChoice, generalChoice, specialistChoice, delayField;
    @FXML
    private Button startButton;

    private SimulatorModel simuModel;

    //method to validate input to only accept number
    private void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
                showAlert("Invalid Input", "Please enter only numbers");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void startButtonAction() {
        try {
            //check if text fields have values and alert if needed
            if (simulationTimeField.getText().isEmpty() || generalTime.getText().isEmpty() || registerTime.getText().isEmpty() || specialistTime.getText().isEmpty() || arrivalTime.getText().isEmpty()) {
                showAlert("Input Required", "Please enter all values");
                return;
            }

            //cast String input to int or double
            int numberRegister = Integer.parseInt(registerChoice.getValue());
            int numberGeneral = Integer.parseInt(generalChoice.getValue());
            int numberSpecialist = Integer.parseInt(specialistChoice.getValue());
            double avgRegisterTime = Double.parseDouble(registerTime.getText());
            double avgGeneralTime = Double.parseDouble(generalTime.getText());
            double avgSpecialistTime = Double.parseDouble(specialistTime.getText());
            double avgArrivalTime = Double.parseDouble(arrivalTime.getText());
            double simulationTime = Double.parseDouble(simulationTimeField.getText());
            long delayTime = Long.parseLong(delayField.getValue());

            //set values for Simulation model
            this.simuModel = new SimulatorModel(numberRegister, avgRegisterTime, numberGeneral, avgGeneralTime, numberSpecialist, avgSpecialistTime, avgArrivalTime);
            this.simuModel.setSimulationTime(simulationTime);

            //load simulation scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/simulator.fxml"));
            Parent root = loader.load();

            //pass values to SimuController
            SimuController simuController = loader.getController();
            simuController.setValues(simuModel, numberRegister, numberGeneral, numberSpecialist, avgRegisterTime, avgGeneralTime, avgSpecialistTime, avgArrivalTime, simulationTime, delayTime);

            //change scene
            Stage stage = (Stage) startButton.getScene().getWindow(); //get the current stage
            stage.setScene(new Scene(root)); //change scene to simulation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        //add options 1 or 2 for all choice box and set default value of 1
        registerChoice.getItems().addAll("1", "2");
        generalChoice.getItems().addAll("1", "2");
        specialistChoice.getItems().addAll("1", "2");
        delayField.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        registerChoice.setValue("1");
        generalChoice.setValue("1");
        specialistChoice.setValue("1");
        delayField.setValue("1");

        //add listeners to 2 text fields to ensure only number entered
        addNumericValidation(simulationTimeField);
        addNumericValidation(registerTime);
        addNumericValidation(generalTime);
        addNumericValidation(specialistTime);
        addNumericValidation(arrivalTime);

        //click start simulation
        startButton.setOnAction(event -> {startButtonAction();});
    }
}