package ecorayen.utils;

import java.sql.*;

public class Myconnection {

    private static Myconnection instance; // Private static instance

    static String url = "jdbc:mysql://localhost:3306/ff16"; // Replace with your DB name
    static String user = "root";
    static String password = "";
    Connection con;

    private Myconnection() { // Private constructor
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Myconnection(String url, String user, String password) { // Private constructor
        try {
            Myconnection.url = url;
            Myconnection.user = user;
            Myconnection.password = password;
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return con;
    }

    public void setConnection(Connection con) {
        this.con = con;
    }

    public static synchronized Myconnection getInstance() { // Synchronized getInstance()
        if (instance == null) {
            instance = new Myconnection();
        }
        return instance;
    }

    public static synchronized Myconnection getInstance(String url, String user, String password) { // Synchronized getInstance() with parameters
        if (instance == null) {
            instance = new Myconnection(url, user, password);
        }
        return instance;
    }
}