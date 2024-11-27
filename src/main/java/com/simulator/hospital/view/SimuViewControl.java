package com.simulator.hospital.view;

import com.simulator.hospital.controller.SimuController;
import com.simulator.hospital.model.*;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimuViewControl {
    //    @FXML
//    public Label registerQueue, generalQueue, specialistQueue, registerLabel1, registerLabel2, registerLabel3, generalLabel1, generalLabel2, generalLabel3, specialistLabel1, specialistLabel2, specialistLabel3, timeLabel;
////    private Button backButton;
    @FXML
    public Label registerQueue, generalQueue, specialistQueue, registerLabel1, registerLabel2, registerLabel3,
            generalLabel1, generalLabel2, generalLabel3, specialistLabel1, specialistLabel2, specialistLabel3, timeLabel;

    @FXML
    private Line registerLine, generalLine, specialistLine;
    @FXML
//    private AnchorPane rootPane;
//    private AnchorPane middlePane;
    private BorderPane rootPane;

    private SimuController controller;
    private double[] registerCoors, generalCoors, specialistCoors, registerQueueCoors, generalQueueCoors, specialistQueueCoors, arrivalCoors, exitCoors;
    private boolean activated = false;
    private Thread simulator = null;
    private HashMap<Integer, CustomerView> customerViewList;

    @FXML
    private void initialize() {
        //start speed monitor thread
        Thread speedMonitorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { // Check for thread interruptions
                if (activated && controller != null) {
                    //long delay = this.speed; //fetch speed from UI (may need to be converted to delay time)
                    //controller.setDelayTime(delay);
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

    public void initializeSimulation(int registerCount, int generalCount, int specialistCount, MainMenuViewControl menuView) {

        controller = new SimuController(menuView, this);
        controller.initializeModel();
        Thread simulatorThread = new Thread(controller);

        // must move controller adn thread to top in order to register coordinate from view to model
        //setup Simulation background
        setupScene(registerCount, generalCount, specialistCount);

        //calculate and set coordinates
        setCoordinates(registerCount, generalCount, specialistCount);

        simulatorThread.start();//start controller thread parallel with UI
        activated = true;

        this.customerViewList = new HashMap<>();
//        this.middlePane = new AnchorPane();
//        this.rootPane.getChildren().add(this.middlePane);

        //mock animation
//        animateCircle(); //can pass time, location, ...
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
                // SUqueue [] = [queueX.queueY]
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


//    private void animateCircle() {
//        Circle movingCircle = new Circle(10); //Create a new circle with radius 10
////        this.middlePane.getChildren().add(movingCircle);
//        this.rootPane.getChildren().add(movingCircle);//Add the circle to the root pane
//
//        Path path = new Path();
//        path.getElements().add(new MoveTo(arrivalCoors[0], arrivalCoors[1])); //Start from arrival point
//        path.getElements().add(new LineTo(registerQueueCoors[0], registerQueueCoors[1]));
//        path.getElements().add(new LineTo(registerCoors[0], registerCoors[1]));
//
//        path.getElements().add(new LineTo(generalQueueCoors[0], generalQueueCoors[1]));
//        path.getElements().add(new LineTo(generalCoors[0], generalCoors[1]));
//
//        path.getElements().add(new LineTo(exitCoors[0], exitCoors[1]));
//
//        PathTransition pathTransition = new PathTransition();
//        pathTransition.setDuration(Duration.seconds(5)); //clock
//        pathTransition.setPath(path);
//        pathTransition.setNode(movingCircle);
//        pathTransition.setCycleCount(PathTransition.INDEFINITE);
//        pathTransition.play();
//    }

//   private void animateCircle2(cus)

    //update scene, run animation method
    @FXML
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
                serviceUnitName = "general";
                newX = generalQueueCoors[0];
                newY = generalQueueCoors[1];
                break;
            case 3:
                serviceUnitName = "specialist";
                newX = specialistQueueCoors[0];
                newY = specialistQueueCoors[1];
                break;
            case 0:
                serviceUnitName = "Exit";
                newX = exitCoors[0];
                newY = exitCoors[1];
//                SnewYstem.out.println(x);

        }
        CustomerView customerView = getCustomerInfo(customerId);
        System.out.println(customerView);
        // this should be set new position
        System.out.println("Customer " + customerId + " move to service unit " + serviceUnitName + ", enter queue at pos = (" + newX + "," + newY + ")");
        customerView.setServiceUnitName(serviceUnitName);
        if (serviceUnitNumber != 0) {
            customerView.setInQueue(true);
        }

        // this should be animation
        this.animateCircle2(customerView, newX, newY);

//        customerView.setX(newX);
//        customerView.setY(newY);


    }

    public void displayBEvent3(Customer customer, ServiceUnit su) {
//        System.out.println("customer id " + customerId + ",service unit number: " + serviceUnitNumber);
        String serviceUnitName;
        double newX, newY;

        int serviceUnitNumber = -1;
        int customerId = customer.getId();

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
        System.out.println(customerView);
        // this should be set new position
        System.out.println("Customer " + customerId + " move to service unit " + serviceUnitName + ", enter queue at pos = (" + newX + "," + newY + ")");
        customerView.setServiceUnitName(serviceUnitName);
        if (serviceUnitNumber != 0) {
            customerView.setInQueue(true);
        }
        this.animateCircle2(customerView, newX, newY);

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

    public void displayCEvent(int customerId, int servicePointId) {
        System.out.printf("Customer %d is being served at service point %d\n", customerId, servicePointId);
    }

    public void displayCEvent2(int customerId, int servicePointId) {
        CustomerView customerView = getCustomerInfo(customerId);
        String serviceUnitName = customerView.getServiceUnitName();
//        System.out.println(customerView);
        System.out.println("customer " + customerId + ", service point " + servicePointId);

        double newX = -1;
        double newY = -1;

        int idx = servicePointId == 1 ? 0 : 2;
        switch (serviceUnitName) {
            case "register":
                newX = registerCoors[idx];
                newY = registerCoors[idx + 1];
                break;
            case "general":
                newX = generalCoors[idx];
                newY = generalCoors[idx + 1];
                break;
            case "specialist":
                newX = specialistCoors[idx];
                newY = specialistCoors[idx + 1];
                break;
        }
        // set to new position
        System.out.println("Customer " + customerId + " move to service point " + servicePointId + ",  pos = (" + newX + "," + newY + ")");
        // animation
        customerView.setInQueue(false);
        this.animateCircle2(customerView, newX, newY);

    }

    public void displayCEvent3(Customer curstomer, ServicePoint sp) {
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
        this.animateCircle2(customerView, newX, newY);
    }

    private void animateCircle2(CustomerView customerView, double newX, double newY) {
        Circle movingCircle = customerView.getCircle();

        if (movingCircle == null) {

            movingCircle = new Circle(10); //Create a new circle with radius 10
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
        pathTransition.setDuration(Duration.millis(300));
        pathTransition.setPath(path);
        pathTransition.setNode(movingCircle);
        // remove the old circle after the animation is finished
        pathTransition.setOnFinished(event -> {
            customerView.setX(newX);
            customerView.setY(newY);
//            rootPane.getChildren().remove(movingCircle);
        });
        pathTransition.play();
    }


}