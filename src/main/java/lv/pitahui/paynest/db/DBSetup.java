package lv.pitahui.paynest.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DBSetup {
    public static void createSampleTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE
            );
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void main(String[] args) {
        try {
            createSampleTable();
            System.out.println("Sample table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
