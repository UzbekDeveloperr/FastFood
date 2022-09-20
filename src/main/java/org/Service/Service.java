package org.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Service {
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "5858";
    private static final String URL = "jdbc:postgresql://localhost:5432/FastFood";

    public Connection connection(){
        try {
            Connection connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
