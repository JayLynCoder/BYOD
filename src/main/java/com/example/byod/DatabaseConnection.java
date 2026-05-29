package com.example.byod;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing().load();
    private static final String DB_URL = dotenv.get("DB_URL", "jdbc:sqlite:byod_system.db");
    private static Connection connection = null;

    public static Connection getConnection() {
        try{
            if (connection == null || connection.isClosed()){
                connection = DriverManager.getConnection(DB_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");
                System.out.println(("Connected to: " + dotenv.get("DB_URL")));
            }
        }catch (SQLException error){
            System.err.println("Connection failed: " + error.getMessage());
        }
        return connection;
    }

    public static void closeConnection(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }catch (SQLException error){
            System.err.println("Error closing: " + error.getMessage());
        }
    }
}
