package fr.fanto.fantodisbot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {


    public static Connection connect() {
        String url = "jdbc:sqlite:database.db"; // Chemin vers la base de données SQLite
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connexion à SQLite établie.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}