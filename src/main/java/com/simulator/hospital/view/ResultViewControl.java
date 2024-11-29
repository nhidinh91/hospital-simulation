package com.simulator.hospital.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultViewControl {

    // FXML UI components
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<ServiceData, String> serviceColumn;
    @FXML
    private TableColumn<ServiceData, Integer> servicePointNumbersColumn;
    @FXML
    private TableColumn<ServiceData, Double> serviceTimeColumn;
    @FXML
    private PieChart totalCustomers;
    @FXML
    private BarChart<String, Number> utilizationChart;
    @FXML
    private Label totalCustomerCount;
    @FXML
    private Label avgWaitingTime;

    private int registers, generals, specialists;
    private double avgRegister, avgGeneral, avgSpecialist;
    private HashMap<String, Double> piechartData = new HashMap<>();
    private ArrayList<Double> registerUtilization = new ArrayList();
    private ArrayList<Double> generalUtilization = new ArrayList<>();
    private ArrayList<Double> specialistUtilization = new ArrayList<>();

    @FXML
    public void initialize() {
        // Set up table columns
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        servicePointNumbersColumn.setCellValueFactory(new PropertyValueFactory<>("servicePointNumbers"));
        serviceTimeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceTime"));
        //Set up BarChart for Utilization Efficiency
        utilizationChart.setTitle("Utilization Efficiency (%)");
        utilizationChart.getXAxis().setLabel("Service Units");
        utilizationChart.getYAxis().setLabel("Utilization");
    }

    // Inner class for table data
    public static class ServiceData {
        private final String service;
        private final int servicePointNumbers;
        private final double serviceTime;

        public ServiceData(String service, int servicePointNumbers, double serviceTime) {
            this.service = service;
            this.servicePointNumbers = servicePointNumbers;
            this.serviceTime = serviceTime;
        }

        public String getService() {
            return service;
        }

        public int getServicePointNumbers() {
            return servicePointNumbers;
        }

        public double getServiceTime() {
            return serviceTime;
        }
    }

    //Draw result data to PieChart and BarChart when they are ready
    public void setResults(double avgWaitTime, List<Integer> customerCount, List<Double> utilization) {
        avgWaitingTime.setText(String.format("%.2f", avgWaitTime));
        totalCustomerCount.setText(String.valueOf(customerCount.stream().mapToInt(Integer::intValue).sum()));

        processStations("RegisterDesk", registers, customerCount, utilization, registerUtilization);
        processStations("GeneralExamination", generals, customerCount, utilization, generalUtilization);
        processStations("SpecialistExamination", specialists, customerCount, utilization, specialistUtilization);

        Platform.runLater(() -> {
            if (totalCustomers != null) {
                piechartData.forEach((key, value) -> totalCustomers.getData().add(new PieChart.Data(key, value)));
            }
            if (utilizationChart != null) {
                utilizationChart.getData().addAll(
                        createUtilizationSeries("RegisterDesk", registerUtilization),
                        createUtilizationSeries("General Examination", generalUtilization),
                        createUtilizationSeries("Specialist Examination", specialistUtilization)
                );
            }
        });
    }
    //Process data from controller to set up PieChart and BarChart
    private void processStations(String stationName, int stationCount, List<Integer> customerCount, List<Double> utilization, List<Double> utilizationList) {
        for (int i = 1; i <= stationCount; i++) {
            piechartData.put(stationName + " - SP" + i, (double) customerCount.remove(0));
            utilizationList.add(utilization.remove(0));
        }
    }
    //Draw data to Utilization Efficiency BarChart
    private XYChart.Series<String, Number> createUtilizationSeries(String name, List<Double> utilizationList) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < utilizationList.size(); i++) {
            series.getData().add(new XYChart.Data<>("Service Point " + (i + 1), utilizationList.get(i)));
        }
        return series;
    }


    //Write data to table when they are loaded
    public void setTable(int registers, int generals, int specialists, double avgRegister, double avgGeneral, double avgSpecialist) {
        this.registers = registers;
        this.generals = generals;
        this.specialists = specialists;
        this.avgRegister = avgRegister;
        this.avgGeneral = avgGeneral;
        this.avgSpecialist = avgSpecialist;
        Platform.runLater(() -> {
            if (tableView != null) {
                tableView.getItems().addAll(
                        new ServiceData("Register Desk", registers, avgRegister),
                        new ServiceData("General Examination", generals, avgGeneral),
                        new ServiceData("Specialist Treatment", specialists, avgSpecialist));
            }
        });
    }


    public void display(double avgWaitTime, List<Integer> customerCount, List<Double> utilization) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/result.fxml"));
            Parent root = loader.load(); // Load the FXML

            ResultViewControl controller = loader.getController(); // Get the controller

            controller.setTable(registers, generals, specialists, avgRegister, avgGeneral, avgSpecialist); // Set the table data
            controller.setResults(avgWaitTime, customerCount, utilization); // Set the results

            Stage stage = new Stage(); // Create a new Stage
            stage.setScene(new Scene(root));
            stage.setTitle("Result View");
            stage.show(); // Display the stage
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}