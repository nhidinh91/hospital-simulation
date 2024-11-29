package com.simulator.hospital.view;

import com.simulator.hospital.controller.SimuController;
import com.simulator.hospital.model.*;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx. scene. Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimuViewControl {

    // FXML Components
    @FXML
    public Label registerQueue, generalQueue, specialistQueue, registerLabel1, registerLabel2, registerLabel3,
            generalLabel1, generalLabel2, generalLabel3, specialistLabel1, specialistLabel2, specialistLabel3, timeLabel;
    @FXML
    public Button backButton;
    @FXML
    private Line registerLine, generalLine, specialistLine;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Slider speedSlider;

    private SimuController controller;
    private boolean activated = false;
    private HashMap<Integer, CustomerView> customerViewList;
    private double[] registerCoors, generalCoors, specialistCoors, registerQueueCoors, generalQueueCoors, specialistQueueCoors, arrivalCoors, exitCoors;
    private Thread simulatorThread;
    private Thread speedMonitorThread;

    /* ========================
          FXML Event Handlers
          ======================== */
    @FXML
    public void backButtonAction(MouseEvent mouseEvent) { //backButton now only go back to mainMenu, not yet reset simulator
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/MainMenu.fxml"));
            Parent mainMenuRoot = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(mainMenuRoot);
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        //start speed monitor thread
        speedMonitorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { // Check for thread interruptions
                if (activated && controller != null) {
                    long delay = (long) speedSlider.getValue() * 1000 ; //fetch speed from UI (may need to be converted to delay time)
                    controller.setDelayTime(delay);
                    try {
                        Thread.sleep(100); // Polling interval for speed adjustments
                    } catch (InterruptedException e) {
                        System.err.println("Speed monitor thread interrupted.");
                        break; // Exit loop on interruption
                    }
                } else {
                    try {
                        Thread.sleep(100); // Wait until simulation is activated
                    } catch (InterruptedException e) {
                        System.err.println("Speed monitor thread interrupted.");
                        break; // Exit loop on interruption
                    }
                }
            }
        });
        speedMonitorThread.start();
    }

    /* ========================
           Initialization Methods
           ======================== */

    public void initializeSimulation(int registerCount, int generalCount, int specialistCount, MainMenuViewControl menuView) {

        this.customerViewList = new HashMap<>();
        controller = new SimuController(menuView, this);
        speedSlider.setValue(((double)controller.getDelayTime() / 1000));
        controller.initializeModel();
        simulatorThread = new Thread(controller);

        // must move controller adn thread to top because setupCoor from view to model need controller
        //setup Simulation background
        setupScene(registerCount, generalCount, specialistCount);

        //calculate and set coordinates
        setCoordinates(registerCount, generalCount, specialistCount);

        simulatorThread.start();//start controller thread parallel with UI
        activated = true;

    }

    private void setupScene(int registerCount, int generalCount, int specialistCount) {
        setServiceUnitVisibility(registerLine, registerLabel1, registerLabel2, registerLabel3, registerCount);
        setServiceUnitVisibility(generalLine, generalLabel1, generalLabel2, generalLabel3, generalCount);
        setServiceUnitVisibility(specialistLine, specialistLabel1, specialistLabel2, specialistLabel3, specialistCount);
        updateSpecialistLabelsBasedOnGeneralCount(generalCount);
    }

    private void setCoordinates(int registerCount, int generalCount, int specialistCount) {
        //wait for the rootPane's layout to complete
        SimulatorModel simulatorModel = this.controller.getSimuModel();

        ServiceUnit registerUnit = simulatorModel.getServiceUnits()[0];
        ServiceUnit generalUnit = simulatorModel.getServiceUnits()[1];
        ServiceUnit specialistUnit = simulatorModel.getServiceUnits()[2];

        ArrayList<ServicePoint> registerSPs = registerUnit.getServicePoints();
        ArrayList<ServicePoint> generalSPs = generalUnit.getServicePoints();
        ArrayList<ServicePoint> specialistSPs = specialistUnit.getServicePoints();


        rootPane.boundsInParentProperty().addListener((observable, oldBounds, newBounds) -> {
            if (rootPane.isVisible()) {
                // Register coordinates
                if (registerCount >= 1) {
                    registerCoors = new double[]{registerLabel1.localToScene(registerLabel1.getBoundsInLocal()).getMinX(), registerLabel1.localToScene(registerLabel1.getBoundsInLocal()).getMinY()};
                }
                if (registerCount == 2) {
                    registerCoors = new double[]{registerLabel2.localToScene(registerLabel2.getBoundsInLocal()).getMinX(), registerLabel2.localToScene(registerLabel2.getBoundsInLocal()).getMinY(), registerLabel3.localToScene(registerLabel3.getBoundsInLocal()).getMinX(), registerLabel3.localToScene(registerLabel3.getBoundsInLocal()).getMinY()};
                }

                // General coordinates
                if (generalCount >= 1) {
                    generalCoors = new double[]{generalLabel1.localToScene(generalLabel1.getBoundsInLocal()).getMinX(), generalLabel1.localToScene(generalLabel1.getBoundsInLocal()).getMinY()};
                }
                if (generalCount == 2) {
                    generalCoors = new double[]{generalLabel2.localToScene(generalLabel2.getBoundsInLocal()).getMinX(), generalLabel2.localToScene(generalLabel2.getBoundsInLocal()).getMinY(), generalLabel3.localToScene(generalLabel3.getBoundsInLocal()).getMinX(), generalLabel3.localToScene(generalLabel3.getBoundsInLocal()).getMinY()};
                }

                // Specialist coordinates
                if (specialistCount >= 1) {
                    specialistCoors = new double[]{specialistLabel1.localToScene(specialistLabel1.getBoundsInLocal()).getMinX(), specialistLabel1.localToScene(specialistLabel1.getBoundsInLocal()).getMinY()};
                }
                if (specialistCount == 2) {
                    specialistCoors = new double[]{specialistLabel2.localToScene(specialistLabel2.getBoundsInLocal()).getMinX(), specialistLabel2.localToScene(specialistLabel2.getBoundsInLocal()).getMinY(), specialistLabel3.localToScene(specialistLabel3.getBoundsInLocal()).getMinX(), specialistLabel3.localToScene(specialistLabel3.getBoundsInLocal()).getMinY()};
                }

                registerQueueCoors = new double[]{registerQueue.localToScene(registerQueue.getBoundsInLocal()).getMinX(), registerQueue.localToScene(registerQueue.getBoundsInLocal()).getMinY()};
                generalQueueCoors = new double[]{generalQueue.localToScene(generalQueue.getBoundsInLocal()).getMinX(), generalQueue.localToScene(generalQueue.getBoundsInLocal()).getMinY()};
                specialistQueueCoors = new double[]{specialistQueue.localToScene(specialistQueue.getBoundsInLocal()).getMinX(), specialistQueue.localToScene(specialistQueue.getBoundsInLocal()).getMinY()};
                arrivalCoors = new double[]{0, rootPane.getHeight() / 2};
                exitCoors = new double[]{rootPane.getWidth(), rootPane.getHeight() / 2};

                //set queue coor in model
                // SUqueue [] = [queueX,queueY]
                this.registerServiceUnitCoordinate(registerUnit, registerQueueCoors);
                this.registerServiceUnitCoordinate(generalUnit, generalQueueCoors);
                this.registerServiceUnitCoordinate(specialistUnit, specialistQueueCoors);


                //set SP coor in model
                // servicePointCoor = [spX1,spY1,spX2,spY2]
                this.registerServicePointsCoordinate(registerSPs, registerCoors);
                this.registerServicePointsCoordinate(generalSPs, generalCoors);
                this.registerServicePointsCoordinate(specialistSPs, specialistCoors);

            }
        });
    }

    /* ========================
       Utility Methods
       ======================== */

    private void setServiceUnitVisibility(Line line, Label label1, Label label2, Label label3, int count) {
        line.setVisible(count == 2);
        label1.setVisible(count >= 1);
        label2.setVisible(count == 2);
        label3.setVisible(count == 2);
    }

    private void updateSpecialistLabelsBasedOnGeneralCount(int generalCount) {
        String label1Text = (generalCount == 1) ? "2" : "3";
        String label2Text = "2";
        String label3Text = (generalCount == 1) ? "3" : "4";

        specialistLabel1.setText(label1Text);
        specialistLabel2.setText(label2Text);
        specialistLabel3.setText(label3Text);
    }

    private void registerServiceUnitCoordinate(ServiceUnit serviceUnit, double[] serviceUnitCoor) {
        serviceUnit.setX((int) serviceUnitCoor[0]);
        serviceUnit.setY((int) serviceUnitCoor[1]);
//        System.out.println("SU " + serviceUnit.getIndex() + " (" + serviceUnit.getX() + "," + serviceUnit.getY() + ")");
    }

    private void registerServicePointsCoordinate(ArrayList<ServicePoint> spList, double[] spCoors) {
        for (int i = 0; i < spList.size(); i++) {
            ServicePoint currenSP = spList.get(i);
            currenSP.setX((int) spCoors[2 * i]);
            currenSP.setY((int) spCoors[2 * i + 1]);
//            System.out.println("SP " + currenSP.getId() + " (" + currenSP.getX() + "," + currenSP.getY() + ")");
        }
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(event -> {
            if (simulatorThread != null && simulatorThread.isAlive()) {
                simulatorThread.interrupt();
            }
            if (speedMonitorThread != null && speedMonitorThread.isAlive()) {
                speedMonitorThread.interrupt();
            }
        });
    }

    /* ========================
       Display and Animation
       ======================== */

    @FXML
    public void displayClock(double time) {
        String timeStr = String.format(Locale.US, "%.2f", time);
        System.out.printf("Clock is at: %s\n", timeStr);
        this.timeLabel.setText(timeStr + " min");
    }

    public void displayBEvent(Customer customer, ServiceUnit su) {
        String serviceUnitName;
        double newX, newY;

        int serviceUnitNumber = -1;
        int customerId = customer.getId();
//        System.out.println("customer type " + customer.getCustomerType());

        if (su == null) {
            serviceUnitName = "exit";
            newX = exitCoors[0];
            newY = exitCoors[1];
        } else {
            serviceUnitNumber = su.getIndex();
            serviceUnitName = getSerViceUnitName(serviceUnitNumber);
            newX = su.getX();
            newY = su.getY();
        }

        CustomerView customerView = getCustomerInfo(customerId);
        customerView.setCustomerType(customer.getCustomerType());
        System.out.println(customerView);
        // this should be set new position
        System.out.println("Customer " + customerId + " move to service unit " + serviceUnitName + ", enter queue at pos = (" + newX + "," + newY + ")");
        customerView.setServiceUnitName(serviceUnitName);
        if (serviceUnitNumber != 0) {
            customerView.setInQueue(true);
        }
        this.animateCirle(customerView, newX, newY);

    }

    public boolean isCustomerExist(int customerid) {
        return this.customerViewList.containsKey(customerid);
    }

    public CustomerView getCustomerInfo(int cusomterId) {
        CustomerView foundCustomer = null;
        // if no customer found, create this customer and add to customerViewList
        if (!this.isCustomerExist(cusomterId)) {
            CustomerView newCustomerView = new CustomerView(cusomterId, arrivalCoors[0], arrivalCoors[1], "arrival");
            this.customerViewList.put(cusomterId, newCustomerView);
            return newCustomerView;
        }
        // if found, return the found customer
        for (Map.Entry<Integer, CustomerView> currentCustomerView : this.customerViewList.entrySet()) {
            int curCustomerId = currentCustomerView.getKey();
            if (curCustomerId == cusomterId) {
                foundCustomer = currentCustomerView.getValue();
            }
        }
        return foundCustomer;

    }

    public static String getSerViceUnitName(int serviceUnitNumber) {
        switch (serviceUnitNumber) {
            case 1:
                return "register";
            case 2:
                return "general";
            case 3:
                return "specialist";
        }
        return null;
    }

    public void displayCEvent(Customer curstomer, ServicePoint sp) {
        int customerId = curstomer.getId();
        int servicePointId = sp.getId();
        CustomerView customerView = getCustomerInfo(customerId);
        String serviceUnitName = customerView.getServiceUnitName();

        double newX = sp.getX();
        double newY = sp.getY();

        // set to new position
        System.out.println("Customer " + customerId + " move to service point " + servicePointId + ",  pos = (" + newX + "," + newY + ")");
        // animation
        customerView.setInQueue(false);
        this.animateCirle(customerView, newX, newY);
    }

    private void animateCirle(CustomerView customerView, double newX, double newY) {
        Circle movingCircle = customerView.getCircle();

        if (movingCircle == null) {

            movingCircle = new Circle(10); //Create a new circle with radius 10
//            moving.set
            rootPane.getChildren().add(movingCircle); //Add the circle to the root pane
            customerView.setCircle(movingCircle);
        }

        double curX = customerView.getX();
        double curY = customerView.getY();
        long delay = controller.getDelayTime();

        Path path = new Path();
        path.getElements().add(new MoveTo(curX, curY));
        path.getElements().add(new LineTo(newX, newY));

        PathTransition pathTransition = new PathTransition();

        //mock duration
//        pathTransition.setDuration(Duration.millis(300));
        //real delay
        // this delay must be shorter than delay in controller to make sure the the ball complete transition before  calculate in C
        // delay = one cycle ABC
        // A B delay/2 C delay/2
        pathTransition.setDuration(Duration.millis(delay* 0.6));

        pathTransition.setPath(path);
        pathTransition.setNode(movingCircle);
        // remove the old circle after the animation is finished
        pathTransition.setOnFinished(event -> {
            customerView.setX(newX);
            customerView.setY(newY);

        });
        pathTransition.play();
    }
}