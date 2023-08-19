package fr.masterofgame09.masterlink.db;

import fr.masterofgame09.masterlink.DisLink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateNewTable {


    public static void createNewTable() {
        // SQLite's connection string
        String url = "jdbc:sqlite:plugins/MasterLink/data.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS link_id (\n"
                + "	id BIGINT NOT NULL,\n"
                + "	name_mc TEXT NOT NULL,\n"
                + "	name_ds TEXT NOT NULL,\n"
                + " date DATETIME DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        String sql2 = "CREATE TABLE IF NOT EXISTS wait_link (\n"
                + "	id BIGINT NOT NULL,\n"
                + "	name_mc TEXT NOT NULL,\n"
                + "	name_ds TEXT NOT NULL,\n"
                + " code INT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
