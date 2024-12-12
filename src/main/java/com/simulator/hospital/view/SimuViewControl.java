package com.simulator.hospital.view;

import com.simulator.hospital.controller.SimuController;

import com.simulator.hospital.model.logic.Customer;
import com.simulator.hospital.model.logic.ServicePoint;
import com.simulator.hospital.model.logic.ServiceUnit;
import com.simulator.hospital.model.logic.SimulatorModel;
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

/**
 * Controller class for managing the simulation view of the hospital simulation application.
 */
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
    private Stage stage;

    /* ========================
          FXML Event Handlers
          ======================== */
    /**
     * Handles the action for the back button.
     * Stops all threads, resets values, and loads the main menu.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void backButtonAction(MouseEvent mouseEvent) {
        try {
            //stop all threads
            if (simulatorThread != null && simulatorThread.isAlive()) {
                simulatorThread.interrupt();
            }
            if (speedMonitorThread != null && speedMonitorThread.isAlive()) {
                speedMonitorThread.interrupt();
            }

            //reset values
            activated = false;
            controller = null;
            registerCoors = null;
            generalCoors = null;
            specialistCoors = null;
            registerQueueCoors = null;
            generalQueueCoors = null;
            specialistQueueCoors = null;
            arrivalCoors = null;
            exitCoors = null;

            //load main menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simulator/hospital/MainMenu.fxml"));
            Parent mainMenuRoot = loader.load();
            stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(mainMenuRoot);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the simulation view control.
     * Starts the speed monitor thread.
     */
    @FXML
    private void initialize() {
        //start speed monitor thread
        speedMonitorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { // Check for thread interruptions
                if (activated && controller != null) {
                    long delay = (long) speedSlider.getValue() ; //fetch speed from UI (may need to be converted to delay time)
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
    /**
     * Initializes the simulation with the specified parameters.
     *
     * @param registerCount the number of register service points
     * @param generalCount the number of general service points
     * @param specialistCount the number of specialist service points
     * @param menuView the main menu view control
     * @param resultView the result view control
     */
    public void initializeSimulation(int registerCount, int generalCount, int specialistCount, MainMenuViewControl menuView, ResultViewControl resultView) {
        this.customerViewList = new HashMap<>();
        controller = new SimuController(menuView, this, resultView);
        speedSlider.setValue(menuView.getDelayTime());
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

    /**
     * Sets up the simulation scene with the specified number of service points.
     *
     * @param registerCount the number of register service points
     * @param generalCount the number of general service points
     * @param specialistCount the number of specialist service points
     */
    private void setupScene(int registerCount, int generalCount, int specialistCount) {
        setServiceUnitVisibility(registerLine, registerLabel1, registerLabel2, registerLabel3, registerCount);
        setServiceUnitVisibility(generalLine, generalLabel1, generalLabel2, generalLabel3, generalCount);
        setServiceUnitVisibility(specialistLine, specialistLabel1, specialistLabel2, specialistLabel3, specialistCount);
        updateSpecialistLabelsBasedOnGeneralCount(generalCount);
    }

    /**
     * Sets the coordinates for the service points and queues.
     *
     * @param registerCount the number of register service points
     * @param generalCount the number of general service points
     * @param specialistCount the number of specialist service points
     */
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

    /**
     * Sets the visibility of the service units based on the count.
     *
     * @param line the line splitting the service units
     * @param label1 the first label
     * @param label2 the second label
     * @param label3 the third label
     * @param count the number of service points
     */
    private void setServiceUnitVisibility(Line line, Label label1, Label label2, Label label3, int count) {
        line.setVisible(count == 2);
        label1.setVisible(count == 1);
        label2.setVisible(count == 2);
        label3.setVisible(count == 2);
    }

    /**
     * Updates the specialist labels based on the general count.
     *
     * @param generalCount the number of general service points
     */
    private void updateSpecialistLabelsBasedOnGeneralCount(int generalCount) {
        String label1Text = (generalCount == 1) ? "2" : "3";
        String label2Text = (generalCount == 1) ? "2" : "3";
        String label3Text = (generalCount == 1) ? "3" : "4";

        specialistLabel1.setText(label1Text);
        specialistLabel2.setText(label2Text);
        specialistLabel3.setText(label3Text);
    }

    /**
     * Registers the coordinates of a service unit.
     *
     * @param serviceUnit the service unit
     * @param serviceUnitCoor the coordinates of the service unit
     */
    private void registerServiceUnitCoordinate(ServiceUnit serviceUnit, double[] serviceUnitCoor) {
        serviceUnit.setX((int) serviceUnitCoor[0]);
        serviceUnit.setY((int) serviceUnitCoor[1]);

    }

    /**
     * Registers the coordinates of service points.
     *
     * @param spList the list of service points
     * @param spCoors the coordinates of the service points
     */
    private void registerServicePointsCoordinate(ArrayList<ServicePoint> spList, double[] spCoors) {
        for (int i = 0; i < spList.size(); i++) {
            ServicePoint currenSP = spList.get(i);
            currenSP.setX((int) spCoors[2 * i]);
            currenSP.setY((int) spCoors[2 * i + 1]);

        }
    }

    /**
     * Sets the close event listener for the stage.
     *
     * @param stage the stage
     */
    public void setCloseEventListener(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            if (simulatorThread != null && simulatorThread.isAlive()) {
                simulatorThread.interrupt();
            }
            if (speedMonitorThread != null && speedMonitorThread.isAlive()) {
                speedMonitorThread.interrupt();
            }
        });
    }

    /**
     * Gets the current stage of the simulation view.
     *
     * @return the current stage
     */
    public Stage getStage() {
        return stage;
    }

    /* ========================
       Display and Animation
       ======================== */

    /**
     * Updates the time label with the given time in minutes.
     *
     * @param time the time to display in minutes
     */
    @FXML
    public void displayClock(double time) {
        String timeStr = String.format(Locale.US, "%.2f", time);

        this.timeLabel.setText(timeStr + " min");
    }

    /**
     * Displays the view of event of a customer moving to a service unit or exiting.
     * Updates the customer's position and animates the movement.
     *
     * @param customer the customer involved in the event
     * @param su the service unit the customer is moving to, or null if exiting
     */
    public void displayBEvent(Customer customer, ServiceUnit su) {
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
        customerView.setCustomerType(customer.getCustomerType());

        customerView.setServiceUnitName(serviceUnitName);
        if (serviceUnitNumber != 0) {
            customerView.setInQueue(true);
        }
        this.animateCirle(customerView, newX, newY);

    }

    /**
     * Checks if a customer with the given ID exists in the customer view list.
     *
     * @param customerid the ID of the customer to check
     * @return true if the customer exists, false otherwise
     */
    public boolean isCustomerExist(int customerid) {
        return this.customerViewList.containsKey(customerid);
    }

    /**
     * Retrieves the customer information for the given customer ID.
     * If the customer does not exist, creates a new customer and adds it to the customer view list.
     *
     * @param cusomterId the ID of the customer
     * @return the CustomerView object for the given customer ID
     */
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


    /**
     * Returns the name of the service unit based on the given service unit number.
     *
     * @param serviceUnitNumber the number of the service unit
     * @return the name of the service unit, or null if the number is not recognized
     */
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
        // change to this because after using back button, all the service unit number is > 3
        // change to service name in number, corresponding to the sergvice in in the model
        //return ""+serviceUnitNumber;
    }


    /**
     * Displays the view of a event of a customer moving to a service point.
     * Updates the customer's position and animates the movement.
     *
     * @param curstomer the customer involved in the event
     * @param sp the service point the customer is moving to
     */
    public void displayCEvent(Customer curstomer, ServicePoint sp) {
        int customerId = curstomer.getId();
        int servicePointId = sp.getId();
        CustomerView customerView = getCustomerInfo(customerId);
        String serviceUnitName = customerView.getServiceUnitName();

        double newX = sp.getX();
        double newY = sp.getY();


        // animation
        customerView.setInQueue(false);
        this.animateCirle(customerView, newX, newY);
    }

    /**
     * Animates the movement of a customer's circle to a new position.
     * If the circle does not exist, it creates a new circle and adds it to the root pane.
     *
     * @param customerView the view of the customer to animate
     * @param newX the new X coordinate for the customer's circle
     * @param newY the new Y coordinate for the customer's circle
     */
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
        pathTransition.setDuration(Duration.millis(delay*0.3));

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