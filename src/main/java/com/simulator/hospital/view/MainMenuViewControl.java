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

/**
 * Controller class for managing the main menu view of the hospital simulation application.
 */
public class MainMenuViewControl {
    @FXML
    public TextField registerTime, generalTime, specialistTime, arrivalTime, simulationTimeField;
    @FXML
    private ChoiceBox<String> registerChoice, generalChoice, specialistChoice, delayField;
    @FXML
    private Button startButton;

    private final SettingsController settingsController = new SettingsController();

    /**
     * Initializes the main menu view control.
     * Sets up choice boxes, numeric validation, loads saved settings, and sets the start button action.
     */
    @FXML
    private void initialize() {
        setupChoiceBoxes(); //add values and default value for REGISTER,GENERAL,SPECIALIST and DELAY
        setupNumericValidation(); //add validator for TextField elements
        loadSavedSettings();
        startButton.setOnAction(event -> {startButtonAction();}); //start simulation
    }

    /**
     * Sets up the choice boxes with values and default selections.
     */
    private void setupChoiceBoxes() {
        registerChoice.getItems().addAll("1", "2");
        generalChoice.getItems().addAll("1", "2");
        specialistChoice.getItems().addAll("1", "2");
        delayField.getItems().addAll("200", "400", "600", "800", "1000", "1200","1400", "1600", "1800", "2000");

        registerChoice.setValue("1");
        generalChoice.setValue("1");
        specialistChoice.setValue("1");
        delayField.setValue("1000");
    }

    /**
     * Sets up numeric validation for the text fields.
     */
    private void setupNumericValidation() {
        addNumericValidation(simulationTimeField);
        addNumericValidation(registerTime);
        addNumericValidation(generalTime);
        addNumericValidation(specialistTime);
        addNumericValidation(arrivalTime);
    }

    /**
     * Adds numeric validation to a text field.
     *
     * @param textField the text field to add validation to
     */
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

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads saved settings from the database and sets the values in the view.
     */
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

    /**
     * Saves the current settings to the database.
     */
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

    /**
     * Handles the action for the start button.
     * Saves the current settings and loads the simulation scene.
     */
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

    /**
     * Gets the number of register service points.
     *
     * @return the number of register service points
     */
    public int getNumberRegister() {return Integer.parseInt(registerChoice.getValue());}

    /**
     * Gets the number of general service points.
     *
     * @return the number of general service points
     */
    public int getNumberGeneral() {
        return Integer.parseInt(generalChoice.getValue());
    }

    /**
     * Gets the number of specialist service points.
     *
     * @return the number of specialist service points
     */
    public int getNumberSpecialist() {
        return Integer.parseInt(specialistChoice.getValue());
    }

    /**
     * Gets the average register time.
     *
     * @return the average register time
     */
    public double getRegisterTime() {
        return Double.parseDouble(this.registerTime.getText());
    }

    /**
     * Gets the average general time.
     *
     * @return the average general time
     */
    public double getGeneralTime() {
        return Double.parseDouble(this.generalTime.getText());
    }

    /**
     * Gets the average specialist time.
     *
     * @return the average specialist time
     */
    public double getSpecialistTime() {
        return Double.parseDouble(this.specialistTime.getText());
    }

    /**
     * Gets the average arrival time.
     *
     * @return the average arrival time
     */
    public double getArrivalTime() {
        return Double.parseDouble(this.arrivalTime.getText());
    }

    /**
     * Gets the simulation time.
     *
     * @return the simulation time
     */
    public double getSimulationTime() {
        return Double.parseDouble(simulationTimeField.getText());
    }

    /**
     * Gets the delay time in milliseconds.
     *
     * @return the delay time in milliseconds
     */
    public long getDelayTime() {
        return Long.parseLong(delayField.getValue());
    }
}