module ecorayen {
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires javafx.fxml; // Add this line for FXML support
    requires javafx.controls; // Add this line for JavaFX Controls

    exports ecorayen; // Export the main package if needed
    exports ecorayen.interfaces;
    exports ecorayen.models;
    exports ecorayen.services;
    exports ecorayen.utils;
    exports ecorayen.controllers; // Export the controllers package

    opens ecorayen.controllers to javafx.fxml; // Open the controllers package to JavaFX FXML
    opens ecorayen to javafx.fxml; // Open the main package to JavaFX FXML if it contains FXML-linked classes
}