package com.simulator.hospital.view;

import com.simulator.hospital.controller.SimuController;
import com.simulator.hospital.view.ResultView;
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
    private CategoryAxis utilizationXAxis;
    @FXML
    private BarChart<String, Number> meanTime;
    @FXML
    private CategoryAxis meanTimeXAxis;
    @FXML
    private Label initialSetupLabel;
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

        // Utilization Efficiency Mock Data
//        utilizationChart.setTitle("Utilization Efficiency (%)");
//        utilizationChart.getXAxis().setLabel("Service Units");
//        utilizationChart.getYAxis().setLabel("Utilization");

//        XYChart.Series<String, Number> registerDeskUtilization = new XYChart.Series<>();
//        registerDeskUtilization.setName("RegisterDesk");
//        registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point 1", 0.26));
//        registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point 2", 0.61));
//
//        XYChart.Series<String, Number> generalExamUtilization = new XYChart.Series<>();
//        generalExamUtilization.setName("General Examination");
//        generalExamUtilization.getData().add(new XYChart.Data<>("Service Point 1", 0.67));
//        generalExamUtilization.getData().add(new XYChart.Data<>("Service Point 2", 0.02));
//
//
//        XYChart.Series<String, Number> specialistExamUtilization = new XYChart.Series<>();
//        specialistExamUtilization.setName("Specialist Examination");
//        specialistExamUtilization.getData().add(new XYChart.Data<>("Service Point 1", 0.46));
//        specialistExamUtilization.getData().add(new XYChart.Data<>("Service Point 2", 0.0));
//
//        utilizationChart.getData().addAll(registerDeskUtilization, generalExamUtilization, specialistExamUtilization);

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

    public void setResults(double avgWaitTime, List<Integer> customerCount, List<Double> utilization) {
        avgWaitingTime.setText(String.format("%.2f", avgWaitTime));
        totalCustomerCount.setText(String.valueOf(customerCount.stream().mapToInt(Integer::intValue).sum()));
        for (Double util : utilization) {
            System.out.println("utilization: " + util);
        }

        if (registers == 1) {
            piechartData.put("RegisterDesk - SP1", (double) customerCount.get(0));
            customerCount.remove(0);
            registerUtilization.add(utilization.get(0));
            utilization.remove(0);
        } else if (registers == 2) {
            piechartData.put("RegisterDesk - SP1", (double) customerCount.get(0));
            customerCount.remove(0);
            piechartData.put("RegisterDesk - SP2", (double) customerCount.get(0));
            customerCount.remove(0);
            registerUtilization.add(utilization.get(0));
            utilization.remove(0);
            registerUtilization.add(utilization.get(0));
            utilization.remove(0);
        }

        if (generals == 1) {
            piechartData.put("GeneralExamination - SP1", (double) customerCount.get(0));
            customerCount.remove(0);
            generalUtilization.add(utilization.get(0));
            utilization.remove(0);
        } else if (generals == 2) {
            piechartData.put("GeneralExamination - SP1", (double) customerCount.get(0));
            customerCount.remove(0);
            piechartData.put("GeneralExamination - SP2", (double) customerCount.get(0));
            customerCount.remove(0);
            generalUtilization.add(utilization.get(0));
            utilization.remove(0);
            generalUtilization.add(utilization.get(0));
            utilization.remove(0);
        }

        if (specialists == 1) {
            piechartData.put("SpecialistExamination - SP1", (double) customerCount.get(0));
            customerCount.remove(0);
            specialistUtilization.add(utilization.get(0));
            utilization.remove(0);
        } else if (specialists == 2) {
            piechartData.put("SpecialistExamination - SP1", (double) customerCount.get(0));
            customerCount.remove(0);
            piechartData.put("SpecialistExamination - SP2", (double) customerCount.get(0));
            customerCount.remove(0);
            specialistUtilization.add(utilization.get(0));
            utilization.remove(0);
            specialistUtilization.add(utilization.get(0));
            utilization.remove(0);
        }

        Platform.runLater(() -> {
            if (totalCustomers != null) {
                for (String key : piechartData.keySet()) {
                    totalCustomers.getData().add(new PieChart.Data(key, piechartData.get(key)));
                }
            }

            if (utilizationChart!= null) {
                utilizationChart.setTitle("Utilization Efficiency (%)");
                utilizationChart.getXAxis().setLabel("Service Units");
                utilizationChart.getYAxis().setLabel("Utilization");
                XYChart.Series<String, Number> registerDeskUtilization = new XYChart.Series<>();
                registerDeskUtilization.setName("RegisterDesk");
                for (int i = 0; i < registerUtilization.size(); i++) {
                    registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point " + i++, registerUtilization.get(i)));
                }
                XYChart.Series<String, Number> generalExamUtilization = new XYChart.Series<>();
                generalExamUtilization.setName("General Examination");
                for (int i = 0; i < generalUtilization.size(); i++) {
                    registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point " + i++, generalUtilization.get(i)));
                }

                XYChart.Series<String, Number> specialistExamUtilization = new XYChart.Series<>();
                specialistExamUtilization.setName("Specialist Examination");
                for (int i = 0; i < specialistUtilization.size(); i++) {
                    registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point " + i++, specialistUtilization.get(i)));
                }

                utilizationChart.getData().addAll(registerDeskUtilization, generalExamUtilization, specialistExamUtilization);
            }
        });
    }

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