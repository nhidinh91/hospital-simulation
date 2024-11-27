package com.simulator.hospital.view;

import com.simulator.hospital.controller.SimuController;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class SimuViewControl {
    @FXML
    public Label registerQueue, generalQueue, specialistQueue, registerLabel1, registerLabel2, registerLabel3,
            generalLabel1, generalLabel2, generalLabel3, specialistLabel1, specialistLabel2, specialistLabel3;
    @FXML
    private Line registerLine, generalLine, specialistLine;
    @FXML
    private BorderPane rootPane;

    @FXML
    private Slider speedSlider;

    private SimuController controller;
    private double[] registerCoors, generalCoors, specialistCoors, registerQueueCoors, generalQueueCoors, specialistQueueCoors, arrivalCoors, exitCoors;
    private boolean activated = false;

    @FXML
    private void initialize() {
        //start speed monitor thread
        Thread speedMonitorThread = new Thread(() -> {
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

    public void initializeSimulation(int registerCount, int generalCount, int specialistCount, MainMenuViewControl menuView) {
        //setup Simulation background
        setupScene(registerCount, generalCount, specialistCount);

        //calculate and set coordinates
        setCoordinates(registerCount, generalCount, specialistCount);
        controller = new SimuController(menuView, this);
        speedSlider.setValue(((double)controller.getDelayTime() / 1000));
        controller.initializeModel();
        Thread simulatorThread = new Thread(controller);
        simulatorThread.start();//start controller thread parallel with UI
        activated = true;

        //mock animation
        //animateCircle(); //can pass time, location, ...
        Platform.runLater(this::animateCircle);
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
                arrivalCoors = new double[]{0, rootPane.getHeight()/2};
                exitCoors = new double[]{rootPane.getWidth(), rootPane.getHeight()/2};
            }
        });
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

    //update scene, run animation method
    public void displayClock(double time) {
        System.out.printf("Clock is at: %.2f\n", time);
    }

    public void displayBEvent(int customerId, int serviceUnitNumber){
        if (serviceUnitNumber != 0) {
            System.out.printf("Customer %d move to queue of Service Unit %d\n", customerId, serviceUnitNumber);
        } else {
            System.out.printf("Customer %d completed service, is removed from system\n", customerId);
        }
    }

    public void displayCEvent(int customerId, int servicePointId) {
        System.out.printf("Customer %d is being served at service point %d\n", customerId, servicePointId);
    }
}