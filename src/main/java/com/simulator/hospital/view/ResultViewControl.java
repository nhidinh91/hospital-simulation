package com.simulator.hospital.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
    @FXML
    private Button backButton;

    private Parent root;
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

    @FXML
    public void backButtonAction(MouseEvent mouseEvent) {
        //switch the scene to MainMenu.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/MainMenu.fxml"));
            Parent mainMenuRoot = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene mainMenuScene = new Scene(mainMenuRoot);
            stage.setScene(mainMenuScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setRoot(Parent resultRoot) {
        this.root = resultRoot;
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
        //Set waiting time and total customer count
        avgWaitingTime.setText(String.format("%.2f", avgWaitTime));
        totalCustomerCount.setText(String.valueOf(customerCount.stream().mapToInt(Integer::intValue).sum()));

        processStations("RegisterDesk", registers, customerCount, utilization, registerUtilization);
        processStations("GeneralExamination", generals, customerCount, utilization, generalUtilization);
        processStations("SpecialistExamination", specialists, customerCount, utilization, specialistUtilization);

        //Set up PieChart and BarChart
        if (totalCustomers != null) {
            piechartData.forEach((key, value) -> totalCustomers.getData().add(new PieChart.Data(key, value)));
        }
        if (utilizationChart != null) {
            XYChart.Series<String, Number> registerDeskUtilization = new XYChart.Series<>();
            registerDeskUtilization.setName("RegisterDesk");
            for (int i = 0; i < registerUtilization.size(); i++) {
                String servicePoint = String.valueOf(i + 1);
                registerDeskUtilization.getData().add(new XYChart.Data<>("Service Point " + servicePoint, registerUtilization.get(i)));
            }

            XYChart.Series<String, Number> generalExamUtilization = new XYChart.Series<>();
            generalExamUtilization.setName("General Examination");
            for (int i = 0; i < generalUtilization.size(); i++) {
                String servicePoint = String.valueOf(i + 1);
                generalExamUtilization.getData().add(new XYChart.Data<>("Service Point " + servicePoint, generalUtilization.get(i)));
            }

            XYChart.Series<String, Number> specialistExamUtilization = new XYChart.Series<>();
            specialistExamUtilization.setName("Specialist Examination");
            for (int i = 0; i < specialistUtilization.size(); i++) {
                String servicePoint = String.valueOf(i + 1);
                specialistExamUtilization.getData().add(new XYChart.Data<>("Service Point " + servicePoint, specialistUtilization.get(i)));
            }

            utilizationChart.getData().addAll(registerDeskUtilization, generalExamUtilization, specialistExamUtilization);
        }
    }
    //Process data from controller to set up PieChart and BarChart
    private void processStations(String stationName, int stationCount, List<Integer> customerCount, List<Double> utilization, List<Double> utilizationList) {
        for (int i = 1; i <= stationCount; i++) {
            piechartData.put(stationName + " - SP" + i, (double) customerCount.remove(0));
            utilizationList.add(utilization.remove(0));
        }
    }

    //Write data to table when they are loaded
    public void setTable(int registers, int generals, int specialists, double avgRegister, double avgGeneral, double avgSpecialist) {
        this.registers = registers;
        this.generals = generals;
        this.specialists = specialists;
        this.avgRegister = avgRegister;
        this.avgGeneral = avgGeneral;
        this.avgSpecialist = avgSpecialist;
            if (tableView != null) {
                tableView.getItems().addAll(
                        new ServiceData("Register Desk", registers, avgRegister),
                        new ServiceData("General Examination", generals, avgGeneral),
                        new ServiceData("Specialist Treatment", specialists, avgSpecialist));
            }
    }


    public void display(double avgWaitTime, List<Integer> customerCount, List<Double> utilization, Stage stage) {
        setResults(avgWaitTime, customerCount, utilization);
        stage.setScene(new Scene(root));
    }
}