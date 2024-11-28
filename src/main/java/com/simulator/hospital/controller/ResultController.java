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
    private BarChart<String, Number> utilization;

    @FXML
    private CategoryAxis utilizationXAxis;

    @FXML
    private BarChart<String, Number> meanTime;

    @FXML
    private CategoryAxis meanTimeXAxis;

    @FXML
    private Label initialSetupLabel;


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

        // Utilization Efficiency Data
        utilization.setTitle("Utilization Efficiency (%)");
        utilization.getXAxis().setLabel("Service Units");
        utilization.getYAxis().setLabel("Utilization");

        XYChart.Series<String, Number> registerDeskUtilization = new XYChart.Series<>();
        registerDeskUtilization.setName("RegisterDesk");
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

        // Set X-Axis categories for Mean Service Time
        meanTimeXAxis.getCategories().addAll(
                "Register Desk SP1", "Register Desk SP2",
                "General Examination SP1", "General Examination SP2",
                "Specialist Examination SP1", "Specialist Examination SP2"
        );

        // Populate Mean Service Time Bar Chart
        XYChart.Series<String, Number> meanTimeSeries = new XYChart.Series<>();
        meanTimeSeries.setName("Mean Service Time");
        meanTimeSeries.getData().add(new XYChart.Data<>("Register Desk SP1", 0.4));
        meanTimeSeries.getData().add(new XYChart.Data<>("Register Desk SP2", 1.2));
        meanTimeSeries.getData().add(new XYChart.Data<>("General Examination SP1", 2.1));
        meanTimeSeries.getData().add(new XYChart.Data<>("General Examination SP2", 0.4));
        meanTimeSeries.getData().add(new XYChart.Data<>("Specialist Examination SP1", 0.2));
        meanTimeSeries.getData().add(new XYChart.Data<>("Specialist Examination SP2", 0.0));
        meanTime.getData().add(meanTimeSeries);

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
