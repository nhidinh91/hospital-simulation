module com.simulator.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens com.simulator.hospital;
    opens com.simulator.hospital.controller;
    opens com.simulator.hospital.model.dao;
    opens com.simulator.hospital.model.entity;
    opens com.simulator.hospital.model.datasource;

    exports com.simulator.hospital.framework;
    exports com.simulator.hospital.model.logic;
    exports com.simulator.hospital.model.entity;
    exports com.simulator.hospital.model.dao;
    exports com.simulator.hospital.model.datasource;
    exports com.simulator.hospital.controller;
    exports com.simulator.hospital.view;
    exports com.simulator.eduni.distributions;
    opens com.simulator.hospital.view to javafx.fxml;
}