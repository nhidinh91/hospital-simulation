package com.simulator.hospital.controller;

import com.simulator.hospital.view.ResultView;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ResultController {

    // FXML UI components
    @FXML
    private TableView<ServiceData> tableView;

    @FXML
    private TableColumn<ServiceData, String> serviceColumn;

    @FXML
    private TableColumn<ServiceData, Integer> servicePointNumbersColumn;

    @FXML
    private TableColumn<ServiceData, Double> serviceTimeColumn;

    @FXML
    private PieChart totalCustomers;

    @FXML
    private StackedBarChart<CategoryAxis, NumberAxis> utilization;

    @FXML
    private StackedBarChart<CategoryAxis, NumberAxis> meanTime;

    @FXML
    private Label initialSetupLabel;


    /*
    // Method to initialize and populate data
    @FXML
    public void initialize() {
        // Set up table columns
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        servicePointNumbersColumn.setCellValueFactory(new PropertyValueFactory<>("servicePointNumbers"));
        serviceTimeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceTime"));

        // Populate table with sample data
        tableView.getItems().addAll(
                new ServiceData("Register Desk", 2, 16.3),
                new ServiceData("General Examination", 1, 8.5),
                new ServiceData("Specialist Treatment", 1, 2.7)
        );

        // Set up PieChart with sample data
        totalCustomers.getData().addAll(
                new PieChart.Data("Register Desk", 8),
                new PieChart.Data("General Examination", 3),
                new PieChart.Data("Specialist Treatment", 4)
        );

        // Add data to StackedBarCharts
        // Add utilization data
        // TODO: Populate with meaningful series data
        // Add mean time data
        // TODO: Populate with meaningful series data
    }
    */
    @FXML
    public void initialize() {
        // Set up table columns
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        servicePointNumbersColumn.setCellValueFactory(new PropertyValueFactory<>("servicePointNumbers"));
        serviceTimeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceTime"));

        // Populate table with sample data
        tableView.getItems().addAll(
                new ServiceData("Register Desk", 2, 16.3),
                new ServiceData("General Examination", 1, 8.5),
                new ServiceData("Specialist Treatment", 1, 2.7)
        );

        // Populate PieChart with total customers data
        totalCustomers.getData().addAll(
                new PieChart.Data("RegisterDesk - SP1", 12),
                new PieChart.Data("RegisterDesk - SP2", 10),
                new PieChart.Data("GeneralExamination - SP1", 9),
                new PieChart.Data("GeneralExamination - SP2", 4),
                new PieChart.Data("SpecialistExamination - SP1", 5),
                new PieChart.Data("SpecialistExamination - SP2", 0)
        );

        // Add data to StackedBarChart 1: Utilization Efficiency (%)
        XYChart.Series<String, Number> registerDeskUtilization = new XYChart.Series<>();
        registerDeskUtilization.setName("Register Desk");
        registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point 1", 0.26));
        registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point 2", 0.61));

        XYChart.Series<String, Number> generalExamUtilization = new XYChart.Series<>();
        generalExamUtilization.setName("General Examination");
        generalExamUtilization.getData().add(new XYChart.Data<>("Service Point 1", 0.67));
        generalExamUtilization.getData().add(new XYChart.Data<>("Service Point 2", 0.02));

        XYChart.Series<String, Number> specialistExamUtilization = new XYChart.Series<>();
        specialistExamUtilization.setName("Specialist Examination");
        specialistExamUtilization.getData().add(new XYChart.Data<>("Service Point 1", 0.46));
        specialistExamUtilization.getData().add(new XYChart.Data<>("Service Point 2", 0.0));

        utilization.getData().addAll(registerDeskUtilization, generalExamUtilization, specialistExamUtilization);

        // Add data to StackedBarChart 2: Mean Service Time (min)
        XYChart.Series<String, Number> registerDeskMeanTime = new XYChart.Series<>();
        registerDeskMeanTime.setName("Register Desk");
        registerDeskMeanTime.getData().add(new XYChart.Data<>("Service Point 1", 0.4));
        registerDeskMeanTime.getData().add(new XYChart.Data<>("Service Point 2", 1.2));

        XYChart.Series<String, Number> generalExamMeanTime = new XYChart.Series<>();
        generalExamMeanTime.setName("General Examination");
        generalExamMeanTime.getData().add(new XYChart.Data<>("Service Point 1", 2.1));
        generalExamMeanTime.getData().add(new XYChart.Data<>("Service Point 2", 0.4));

        XYChart.Series<String, Number> specialistExamMeanTime = new XYChart.Series<>();
        specialistExamMeanTime.setName("Specialist Examination");
        specialistExamMeanTime.getData().add(new XYChart.Data<>("Service Point 1", 0.2));
        specialistExamMeanTime.getData().add(new XYChart.Data<>("Service Point 2", Double.NaN)); // NaN for missing data

        meanTime.getData().addAll(registerDeskMeanTime, generalExamMeanTime, specialistExamMeanTime);


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

    public static void main(String[] args) {
        ResultView.launch(ResultView.class);
    }
}
