module lk.ijse.sams {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires lombok;
    requires java.sql;
    requires java.naming;
    requires java.desktop;
    requires jbcrypt;

    // Export main package so javafx.graphics can instantiate AppInitializer
    exports lk.ijse.sams;
    exports lk.ijse.sams.controller;
    exports lk.ijse.sams.dto;
    exports lk.ijse.sams.dto.tm;
    exports lk.ijse.sams.entity;
    exports lk.ijse.sams.util;

    // Open packages for reflection (FXML loader + Hibernate)
    opens lk.ijse.sams        to javafx.graphics, javafx.fxml;
    opens lk.ijse.sams.controller to javafx.fxml;
    opens lk.ijse.sams.entity to org.hibernate.orm.core, javafx.base;
    opens lk.ijse.sams.dto    to javafx.base, javafx.fxml;
    opens lk.ijse.sams.dto.tm to javafx.base;
    opens lk.ijse.sams.util   to javafx.fxml;
}
