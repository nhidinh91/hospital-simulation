package com.simulator.hospital.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SimuController {
    @FXML
    public Label registerQueue;
    @FXML
    public Label generalQueue;
    @FXML
    public Label specialistQueue;
    @FXML
    private Button backButton;
    @FXML
    private Line registerLine;
    @FXML
    private Line generalLine;
    @FXML
    private Line specialistLine;
    @FXML
    private Label registerLabel1;
    @FXML
    private Label registerLabel2;
    @FXML
    private Label registerLabel3;
    @FXML
    private Label generalLabel1;
    @FXML
    private Label generalLabel2;
    @FXML
    private Label generalLabel3;
    @FXML
    private Label specialistLabel1;
    @FXML
    private Label specialistLabel2;
    @FXML
    private Label specialistLabel3;
    @FXML
    private AnchorPane rootPane;

    private double[] registerCoors;
    private double[] generalCoors;
    private double[] specialistCoors;
    private double[] registerQueueCoors;
    private double[] generalQueueCoors;
    private double[] specialistQueueCoors;
    private double[] arrivalCoors;
    private double[] exitCoors;

    public void setValues(int registerCount, int generalCount, int specialistCount) {
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

        //calculate and set coordinates
        setCoordinates(registerCount, generalCount, specialistCount);

        //mock animation
        animateCircle(); //can pass time, location, ...
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

    @FXML
    private void initialize() {

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
}