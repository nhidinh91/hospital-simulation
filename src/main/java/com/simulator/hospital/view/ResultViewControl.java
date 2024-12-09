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

/**
 * Controller for the Result View of the hospital simulator.
 * Handles the display and interaction logic for the result screen,
 * including charts, tables, and navigation.
 *
 * @author Tu Dinh
 */
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
    private ArrayList<Double> registerUtilization = new ArrayList<>();
    private ArrayList<Double> generalUtilization = new ArrayList<>();
    private ArrayList<Double> specialistUtilization = new ArrayList<>();

    /**
     * Initializes the ResultViewControl and sets up the UI components.
     * Configures table columns and initializes the bar chart with appropriate labels.
     */
    @FXML
    public void initialize() {
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        servicePointNumbersColumn.setCellValueFactory(new PropertyValueFactory<>("servicePointNumbers"));
        serviceTimeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceTime"));
        utilizationChart.setTitle("Utilization Efficiency (%)");
        utilizationChart.getXAxis().setLabel("Service Units");
        utilizationChart.getYAxis().setLabel("Utilization");
    }

    /**
     * Handles the action of clicking the back button.
     * Switches the scene back to the main menu.
     *
     * @param mouseEvent the mouse event triggered by clicking the back button
     */
    @FXML
    public void backButtonAction(MouseEvent mouseEvent) {
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

    /**
     * Sets the root node for the result view scene.
     *
     * @param resultRoot the root node for the scene
     */
    public void setRoot(Parent resultRoot) {
        this.root = resultRoot;
    }

    /**
     * Represents a single row of data in the results table.
     */
    public static class ServiceData {
        private final String service;
        private final int servicePointNumbers;
        private final double serviceTime;

        /**
         * Constructs a ServiceData object.
         *
         * @param service            the name of the service
         * @param servicePointNumbers the number of service points
         * @param serviceTime         the average service time
         */
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

    /**
     * Sets and displays the results data in the charts and labels.
     *
     * @param avgWaitTime  the average waiting time
     * @param customerCount the list of customer counts per station
     * @param utilization   the list of utilization percentages per station
     */
    public void setResults(double avgWaitTime, List<Integer> customerCount, List<Double> utilization) {
        avgWaitingTime.setText(String.format("%.2f", avgWaitTime));
        totalCustomerCount.setText(String.valueOf(customerCount.stream().mapToInt(Integer::intValue).sum()));

        processStations("RegisterDesk", registers, customerCount, utilization, registerUtilization);
        processStations("GeneralExamination", generals, customerCount, utilization, generalUtilization);
        processStations("SpecialistExamination", specialists, customerCount, utilization, specialistUtilization);

        if (totalCustomers != null) {
            piechartData.forEach((key, value) -> totalCustomers.getData().add(new PieChart.Data(key, value)));
        }
        if (utilizationChart != null) {
            XYChart.Series<String, Number> registerDeskUtilization = createChartSeries("RegisterDesk", registerUtilization);
            XYChart.Series<String, Number> generalExamUtilization = createChartSeries("General Examination", generalUtilization);
            XYChart.Series<String, Number> specialistExamUtilization = createChartSeries("Specialist Examination", specialistUtilization);
            utilizationChart.getData().addAll(registerDeskUtilization, generalExamUtilization, specialistExamUtilization);
        }
    }

    /**
     * Processes station data and prepares it for charts.
     *
     * @param stationName    the name of the station
     * @param stationCount   the number of service points at the station
     * @param customerCount  the list of customer counts
     * @param utilization    the list of utilization percentages
     * @param utilizationList the list to store utilization data
     */
    private void processStations(String stationName, int stationCount, List<Integer> customerCount, List<Double> utilization, List<Double> utilizationList) {
        for (int i = 1; i <= stationCount; i++) {
            piechartData.put(stationName + " - SP" + i, (double) customerCount.remove(0));
            utilizationList.add(utilization.remove(0));
        }
    }

    /**
     * Populates the table with service data.
     *
     * @param registers     the number of register desks
     * @param generals      the number of general examination points
     * @param specialists   the number of specialist examination points
     * @param avgRegister   the average utilization of register desks
     * @param avgGeneral    the average utilization of general examination points
     * @param avgSpecialist the average utilization of specialist examination points
     */
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

    /**
     * Displays the result view on the specified stage.
     *
     * @param avgWaitTime  the average waiting time
     * @param customerCount the list of customer counts
     * @param utilization   the list of utilization percentages
     * @param stage         the stage to display the scene on
     */
    public void display(double avgWaitTime, List<Integer> customerCount, List<Double> utilization, Stage stage) {
        setResults(avgWaitTime, customerCount, utilization);
        stage.setScene(new Scene(root));
    }

    /**
     * Creates a data series for the bar chart.
     *
     * @param name          the name of the series
     * @param utilizationList the list of utilization data
     * @return the created data series
     */
    private XYChart.Series<String, Number> createChartSeries(String name, List<Double> utilizationList) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < utilizationList.size(); i++) {
            String servicePoint = String.valueOf(i + 1);
            series.getData().add(new XYChart.Data<>("Service Point " + servicePoint, utilizationList.get(i)));
        }
        return series;
    }
}
