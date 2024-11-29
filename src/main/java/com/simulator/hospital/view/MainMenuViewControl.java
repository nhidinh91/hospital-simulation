package com.simulator.hospital.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuViewControl {
    @FXML
    public TextField registerTime, generalTime, specialistTime, arrivalTime, simulationTimeField;
    @FXML
    private ChoiceBox<String> registerChoice, generalChoice, specialistChoice, delayField;
    @FXML
    private Button startButton;

    //INIT METHOD
    @FXML
    private void initialize() {
        setupChoiceBoxes(); //add values and default value for REGISTER,GENERAL,SPECIALIST and DELAY
        setupNumericValidation(); //add validator for TextField elements
        startButton.setOnAction(event -> {startButtonAction();}); //start simulation
    }

    //PRIVATE METHODS
    private void setupChoiceBoxes() {
        registerChoice.getItems().addAll("1", "2");
        generalChoice.getItems().addAll("1", "2");
        specialistChoice.getItems().addAll("1", "2");
        delayField.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        registerChoice.setValue("1");
        generalChoice.setValue("1");
        specialistChoice.setValue("1");
        delayField.setValue("1");
    }

    private void setupNumericValidation() {
        addNumericValidation(simulationTimeField);
        addNumericValidation(registerTime);
        addNumericValidation(generalTime);
        addNumericValidation(specialistTime);
        addNumericValidation(arrivalTime);
    }

    private void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { //validate input to only accept number
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

            //load simulation scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/nsimulator.fxml"));
            Parent root = loader.load();

            //load result scene
            FXMLLoader resultLoader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/result.fxml"));
            Parent resultRoot = resultLoader.load();
            ResultViewControl resultView = resultLoader.getController();
            resultView.setRoot(resultRoot);

            //pass values to SimuViewControl
            SimuViewControl simuViewControl = loader.getController();
            simuViewControl.initializeSimulation(getNumberRegister(), getNumberGeneral(), getNumberSpecialist(), this, resultView);

            //change scene
            Stage stage = (Stage) startButton.getScene().getWindow(); //get the current stage
            simuViewControl.setCloseEventListener(stage);
            stage.setScene(new Scene(root)); //change scene to simulation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //PUBLIC GETTER METHODS
    public int getNumberRegister() {return Integer.parseInt(registerChoice.getValue());}

    public int getNumberGeneral() {
        return Integer.parseInt(generalChoice.getValue());
    }

    public int getNumberSpecialist() {
        return Integer.parseInt(specialistChoice.getValue());
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
        return Double.parseDouble(simulationTimeField.getText());
    }

    public long getDelayTime() {
        return Long.parseLong(delayField.getValue())*1000;
    }
}