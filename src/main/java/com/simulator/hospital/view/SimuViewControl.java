package com.simulator.hospital.view;

import com.simulator.hospital.controller.SimuController;
import com.simulator.hospital.model.CustomerView;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimuViewControl {
    @FXML
    public Label registerQueue, generalQueue, specialistQueue, registerLabel1, registerLabel2, registerLabel3, generalLabel1, generalLabel2, generalLabel3, specialistLabel1, specialistLabel2, specialistLabel3, timeLabel;
    @FXML
    private Button backButton;
    @FXML
    private Line registerLine, generalLine, specialistLine;
    @FXML
    private AnchorPane rootPane;

    private MainMenuViewControl menuView;
    private SimuController controller;
    private double[] registerCoors, generalCoors, specialistCoors, registerQueueCoors, generalQueueCoors, specialistQueueCoors, arrivalCoors, exitCoors;
    private boolean activated = false;
    private Thread simulator = null;
    private HashMap<Integer, CustomerView> customerViewList;

    @FXML
    private void initialize() {
        // Monitor the activation status in a new thread
        new Thread(() -> {
            while (!activated) {
                try {
                    Thread.sleep(100); // Wait until activated
                } catch (InterruptedException e) {
                    System.err.println("Waiting thread interrupted");
                }
            }
            // During simulation, if the user changes the value of delay time
            while (simulator != null && simulator.isAlive()) {
                long delay = menuView.getDelayTime();
                controller.setDelayTime(delay);
                try {
                    Thread.sleep(100); // Avoid constant polling
                } catch (InterruptedException e) {
                    System.err.println("Delay adjustment thread interrupted");
                }
            }
        }).start();

        //Back button clicked
        backButton.setOnAction(event -> {
            try {
                //set scene back to Main menu
                Parent root = FXMLLoader.load(getClass().getResource("/com/simulator/hospital/mainMenu.fxml"));
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initializeSimulation(int registerCount, int generalCount, int specialistCount, MainMenuViewControl menuView) {
        this.menuView = menuView;
        //setup Simulation background
        setupScene(registerCount, generalCount, specialistCount);
        //calculate and set coordinates
        setCoordinates(registerCount, generalCount, specialistCount);

        controller = new SimuController(menuView, this);
        controller.initializeModel();
        simulator = new Thread(controller);
        simulator.start(); //start controller thread parallel with UI
        activated = true;

        this.customerViewList = new HashMap<>();

        //mock animation
        animateCircle(); //can pass time, location, ...
    }

    private void setupScene(int registerCount, int generalCount, int specialistCount) {
        registerLine.setVisible(registerCount == 2);
        generalLine.setVisible(generalCount == 2);
        specialistLine.setVisible(specialistCount == 2);

        registerLabel1.setVisible(registerCount == 1);
        registerLabel2.setVisible(registerCount == 2);
        registerLabel3.setVisible(registerCount == 2);

        generalLabel1.setVisible(generalCount == 1);
        generalLabel2.setVisible(generalCount == 2);
        generalLabel3.setVisible(generalCount == 2);

        if (generalCount == 1) {
            specialistLabel1.setText("2");
            specialistLabel2.setText("2");
            specialistLabel3.setText("3");
        } else if (generalCount == 2) {
            specialistLabel1.setText("3");
            specialistLabel2.setText("3");
            specialistLabel3.setText("4");
        }

        specialistLabel1.setVisible(specialistCount == 1);
        specialistLabel2.setVisible(specialistCount == 2);
        specialistLabel3.setVisible(specialistCount == 2);
    }

    private void setCoordinates(int registerCount, int generalCount, int specialistCount) {
        if (registerCount >= 1) {
            registerCoors = new double[]{registerLabel1.localToScene(registerLabel1.getBoundsInLocal()).getMinX(), registerLabel1.localToScene(registerLabel1.getBoundsInLocal()).getMinY()};
        }
        if (registerCount == 2) {
            registerCoors = new double[]{registerLabel2.localToScene(registerLabel2.getBoundsInLocal()).getMinX(), registerLabel2.localToScene(registerLabel2.getBoundsInLocal()).getMinY(), registerLabel3.localToScene(registerLabel3.getBoundsInLocal()).getMinX(), registerLabel3.localToScene(registerLabel3.getBoundsInLocal()).getMinY()};
        }

        if (generalCount >= 1) {
            generalCoors = new double[]{generalLabel1.localToScene(generalLabel1.getBoundsInLocal()).getMinX(), generalLabel1.localToScene(generalLabel1.getBoundsInLocal()).getMinY()};
        }
        if (generalCount == 2) {
            generalCoors = new double[]{generalLabel2.localToScene(generalLabel2.getBoundsInLocal()).getMinX(), generalLabel2.localToScene(generalLabel2.getBoundsInLocal()).getMinY(), generalLabel3.localToScene(generalLabel3.getBoundsInLocal()).getMinX(), generalLabel3.localToScene(generalLabel3.getBoundsInLocal()).getMinY()};
        }

        if (specialistCount >= 1) {
            specialistCoors = new double[]{specialistLabel1.localToScene(specialistLabel1.getBoundsInLocal()).getMinX(), specialistLabel1.localToScene(specialistLabel1.getBoundsInLocal()).getMinY()};
        }
        if (specialistCount == 2) {
            specialistCoors = new double[]{specialistLabel2.localToScene(specialistLabel2.getBoundsInLocal()).getMinX(), specialistLabel2.localToScene(specialistLabel2.getBoundsInLocal()).getMinY(), specialistLabel3.localToScene(specialistLabel3.getBoundsInLocal()).getMinX(), specialistLabel3.localToScene(specialistLabel3.getBoundsInLocal()).getMinY()};
        }
        registerQueueCoors = new double[]{registerQueue.localToScene(registerQueue.getBoundsInLocal()).getMinX(), registerQueue.localToScene(registerQueue.getBoundsInLocal()).getMinY()};
        generalQueueCoors = new double[]{generalQueue.localToScene(generalQueue.getBoundsInLocal()).getMinX(), generalQueue.localToScene(generalQueue.getBoundsInLocal()).getMinY()};
        specialistQueueCoors = new double[]{specialistQueue.localToScene(specialistQueue.getBoundsInLocal()).getMinX(), specialistQueue.localToScene(specialistQueue.getBoundsInLocal()).getMinY()};
        arrivalCoors = new double[]{0, 400};
        exitCoors = new double[]{1280, 400};

        //set up coordinates for models
        //....
    }


    private void animateCircle() {
        Circle movingCircle = new Circle(10); //Create a new circle with radius 10
        rootPane.getChildren().add(movingCircle); //Add the circle to the root pane

        Path path = new Path();
        path.getElements().add(new MoveTo(arrivalCoors[0], arrivalCoors[1])); //Start from arrival point
        path.getElements().add(new LineTo(registerQueueCoors[0], registerQueueCoors[1]));
        path.getElements().add(new LineTo(registerCoors[0], registerCoors[1]));

        path.getElements().add(new LineTo(generalQueueCoors[0], generalQueueCoors[1]));
        path.getElements().add(new LineTo(generalCoors[0], generalCoors[1]));

        path.getElements().add(new LineTo(exitCoors[0], exitCoors[1]));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(5)); //clock
        pathTransition.setPath(path);
        pathTransition.setNode(movingCircle);
        pathTransition.setCycleCount(PathTransition.INDEFINITE);
        pathTransition.play();
    }

//   private void animateCircle2(cus)

    public void displayClock(double time) {
//        System.out.printf("Clock is at: %.2f\n", time);
        String timeStr = String.format(Locale.US, "%.2f", time);
        System.out.printf("Clock is at: %s\n", timeStr);
        this.timeLabel.setText(timeStr + " min");
    }

    public void displayBEvent(int customerId, int serviceUnitNumber) {
        if (serviceUnitNumber != 0) {
            System.out.printf("Customer %d move to queue of Service Unit %d\n", customerId, serviceUnitNumber);
        } else {
            System.out.printf("Customer %d completed service, is removed from system\n", customerId);
        }
    }

    public void displayBEvent2(int customerId, int serviceUnitNumber) {
//        System.out.println("customer id " + customerId + ",service unit number: " + serviceUnitNumber);
        String serviceUnitName = "";
        double newX = -1;
        double newY = -1;
        switch (serviceUnitNumber) {
            case 1:
                serviceUnitName = "register";
                newX = registerQueueCoors[0];
                newY = registerQueueCoors[1];
                break;
            case 2:
                serviceUnitName = "regular";
                newX = generalQueueCoors[0];
                newY = generalQueueCoors[1];
                break;
            case 3:
                serviceUnitName = "specialist";
                newX = specialistQueueCoors[0];
                newY = specialistQueueCoors[1];
                break;
            case 0:
                serviceUnitName = "EnewXit";
                newX = exitCoors[0];
                newY = exitCoors[1];
//                SnewYstem.out.println(x);

        }
        CustomerView customerView = getCustomerInfo(customerId);
        System.out.println(customerView);

        // this should be animation

        // this should be set new position
        System.out.println("Customer " + customerId + " move to service unit " + serviceUnitName + ", enter queue at pos = (" + newX + "," + newY + ")");
        customerView.setServiceUnitName(serviceUnitName);
        if (serviceUnitNumber != 0) {
            customerView.setInQueue(true);
        }
        customerView.setX(newX);
        customerView.setY(newY);


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
            case 0:
                return "exit";
            case 1:
                return "register";
            case 2:
                return "general";
            case 3:
                return "specialist";
        }
        return null;
    }

    public void displayCEvent(int customerId, int servicePointId) {
        System.out.printf("Customer %d is being served at service point %d\n", customerId, servicePointId);
    }

    public void displayCEvent2(int customerId, int servicePointId) {
        CustomerView customerView = getCustomerInfo(customerId);
        String serviceUnitName = customerView.getServiceUnitName();
        System.out.println(customerView);
        System.out.println("customer "+customerId+", service point "+servicePointId);

//        double newX = -1;
//        double newY = -1;
//
//        int idx = servicePointId == 0 ? 0 : 2;
//        switch (serviceUnitName) {
//            case "register":
//                newX = registerCoors[idx];
//                newY = registerCoors[idx + 1];
//                break;
//            case "general":
//                newX = generalCoors[idx];
//                newY = generalCoors[idx + 1];
//                break;
//            case "specialist":
//                newX = specialistCoors[idx];
//                newY = specialistCoors[idx + 1];
//                break;
//        }
//        // animation
//
//        // set to new position
//        System.out.println("Customer " + customerId + " move to service point " + servicePointId + ",  pos = (" + newX + "," + newY + ")");
////        customerView.setServiceUnitName(serviceUnitName);
//
//        customerView.setInQueue(false);
//        customerView.setX(newX);
//        customerView.setY(newY);


    }
}