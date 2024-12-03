package com.simulator.hospital.view;

import com.simulator.hospital.controller.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainMenuViewControl {
    @FXML
    public TextField registerTime, generalTime, specialistTime, arrivalTime, simulationTimeField;
    @FXML
    private ChoiceBox<String> registerChoice, generalChoice, specialistChoice, delayField;
    @FXML
    private Button startButton;

    private final SettingsController settingsController = new SettingsController();

    //INIT METHOD
    @FXML
    private void initialize() {
        setupChoiceBoxes(); //add values and default value for REGISTER,GENERAL,SPECIALIST and DELAY
        setupNumericValidation(); //add validator for TextField elements
        loadSavedSettings();
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
            //allow empty input or valid numbers with decimals
            if (newValue.isEmpty() || newValue.matches("\\d+(\\.\\d+)?")) {
                return;
            }
            //revert to the previous valid value
            textField.setText(oldValue);

            //show alert only if the user enters invalid characters
            if (!newValue.isEmpty()) {
                showAlert("Invalid Input", "Please enter only numbers.");
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

    //fetch data from database if available
    private void loadSavedSettings() {
        Map<String, Object> settings = settingsController.loadSettings();
        if (settings.containsKey("DelayTime")) delayField.setValue(String.valueOf(settings.get("DelayTime")));
        if (settings.containsKey("ArrivalTime")) arrivalTime.setText(String.valueOf(settings.get("ArrivalTime")));
        if (settings.containsKey("RegisterTime")) registerTime.setText(String.valueOf(settings.get("RegisterTime")));
        if (settings.containsKey("GeneralTime")) generalTime.setText(String.valueOf(settings.get("GeneralTime")));
        if (settings.containsKey("SpecialistTime")) specialistTime.setText(String.valueOf(settings.get("SpecialistTime")));
        if (settings.containsKey("SimulationTime")) simulationTimeField.setText(String.valueOf(settings.get("SimulationTime")));
        if (settings.containsKey("Register")) registerChoice.setValue(String.valueOf(settings.get("Register")));
        if (settings.containsKey("General")) generalChoice.setValue(String.valueOf(settings.get("General")));
        if (settings.containsKey("Specialist")) specialistChoice.setValue(String.valueOf(settings.get("Specialist")));
    }

    //save current settings to database
    private void saveCurrentSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("DelayTime", Long.parseLong(delayField.getValue()));
        settings.put("ArrivalTime", Double.parseDouble(arrivalTime.getText()));
        settings.put("RegisterTime", Double.parseDouble(registerTime.getText()));
        settings.put("GeneralTime", Double.parseDouble(generalTime.getText()));
        settings.put("SpecialistTime", Double.parseDouble(specialistTime.getText()));
        settings.put("SimulationTime", Double.parseDouble(simulationTimeField.getText()));
        settings.put("Register", Integer.parseInt(registerChoice.getValue()));
        settings.put("General", Integer.parseInt(generalChoice.getValue()));
        settings.put("Specialist", Integer.parseInt(specialistChoice.getValue()));

        settingsController.saveSettings(settings);
    }

    private void startButtonAction() {
        try {
            //check if text fields have values and alert if needed
            if (simulationTimeField.getText().isEmpty() || generalTime.getText().isEmpty() || registerTime.getText().isEmpty() || specialistTime.getText().isEmpty() || arrivalTime.getText().isEmpty()) {
                showAlert("Input Required", "Please enter all values");
                return;
            }
            //save current setting to SettingsController
            saveCurrentSettings();

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