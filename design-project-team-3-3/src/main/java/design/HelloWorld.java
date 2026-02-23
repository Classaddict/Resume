package design;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class HelloWorld {
public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/swen344";
        String user = "postgres";
        String password = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the PostgreSQL database successfully!");
        } catch (SQLException e) {
            System.err.println("Connection failure.");
            e.printStackTrace();
        }
    }
}
