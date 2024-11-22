module com.simulator.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.simulator.hospital to javafx.fxml;
    exports com.simulator.hospital.framework;
    exports com.simulator.hospital.model;
    exports com.simulator.hospital.controller;
    exports com.simulator.hospital.view;
    exports com.simulator.eduni.distributions;
}