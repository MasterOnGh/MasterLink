package fr.masterofgame09.masterlink.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class getConnection {

    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:plugins/MasterLink/data.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }

        return conn;
    }
}
