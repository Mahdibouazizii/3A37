module ecorayen {
    // JavaFX Modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires javafx.media;
    requires javafx.swing;

    // Database
    requires java.sql;

    // 3rd-Party Libraries
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires io.nayuki.qrcodegen;

    // OpenHTMLToPDF (Modern HTML-to-PDF)
    requires openhtmltopdf.core;    // Automatic module name from JAR
    requires openhtmltopdf.pdfbox;
    requires org.json;
    requires okhttp3;
    requires jdk.jsobject;  // Automatic module name from JAR

    // Exports
    exports ecorayen;
    exports ecorayen.interfaces;
    exports ecorayen.models;
    exports ecorayen.services;
    exports ecorayen.utils;
    exports ecorayen.controllers;

    // Opens for Reflection (FXML, etc.)
    opens ecorayen.controllers to javafx.fxml, javafx.swing;
    opens ecorayen to javafx.fxml;
    opens ecorayen.models to javafx.base;
    opens ecorayen.services to javafx.base;
}