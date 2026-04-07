package lv.pitahui.paynest.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSetup {

    public static void initializeTables() throws SQLException {
        String sql = """
            PRAGMA foreign_keys = ON;

            CREATE TABLE IF NOT EXISTS Lietotajs (
                Lietotaja_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Vards TEXT(60),
                Uzvards TEXT(60),
                Talrunis TEXT(15) UNIQUE,
                IBAN TEXT(32) UNIQUE,
                Password TEXT(100)
            );

            CREATE TABLE IF NOT EXISTS Abonements (
                Abonementa_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nosaukums TEXT(200),
                Veids TEXT(100),
                Cena DECIMAL(10, 2),
                Ilgums INTEGER,
                Lietotaja_ID INTEGER,
                Aktivizacijas_datums DATE,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            CREATE TABLE IF NOT EXISTS Maksajums (
                Maksajuma_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Abonementa_ID INTEGER,
                Lietotaja_ID INTEGER,
                Summa DECIMAL(10, 2),
                Datums_un_Laiks DATETIME,
                Statuss TEXT(30),
                FOREIGN KEY (Abonementa_ID) REFERENCES Abonements(Abonementa_ID),
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            CREATE TABLE IF NOT EXISTS Pazinojums (
                Pazinojuma_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER,
                Abonementa_ID INTEGER,
                Teksts TEXT(300),
                Izveides_datums Date,
                Dienas_lidz_terminam INTEGER,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID),
                FOREIGN KEY (Abonementa_ID) REFERENCES Abonements(Abonementa_ID)
            );

            CREATE TABLE IF NOT EXISTS Kartes (
                Karte_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER,
                Kartes_numurs TEXT(64),
                Derigums TEXT(10),
                Kartes_vards TEXT(120),
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            CREATE TABLE IF NOT EXISTS Bankas_konts (
                Bankas_konts_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER UNIQUE,
                Bilance DECIMAL(10, 2),
                Kartes_ID INTEGER,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID),
                FOREIGN KEY (Kartes_ID) REFERENCES Kartes(Karte_ID)
            );

            CREATE TABLE IF NOT EXISTS Kopejas_izmaksas (
                Kopejas_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER,
                Menesis TEXT(7), -- format YYYY-MM
                Summa DECIMAL(12,2),
                Izveides_datums DATETIME,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            
            // Only insert sample data if the Lietotajs table is empty
            if (isTableEmpty("Lietotajs")) {
                String sampleData = """
                    -- sample user (owner for subscriptions)
                    INSERT INTO Lietotajs (Vards, Uzvards, Talrunis, IBAN, Password) VALUES ('Admin', 'User', '0000', 'LV00TEST000000000000', 'admin');

                    -- sample bank account for admin user
                    INSERT INTO Bankas_konts (Lietotaja_ID, Bilance) VALUES (1, 500.00);

                    -- sample subscriptions (owned by the sample user id=1)
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Spotify', 'Basic monthly', 5.99, 30,1, date('2026-04-02'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Spotify', 'Basic monthly', 5.99, 30, 1, date('2026-03-09'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Kling.ai', 'Standard monthly', 9.99, 30, 1, date('2026-03-04'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Kling.ai', 'Premium monthly', 14.99, 30, 1, date('2026-03-12'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Spotify', 'Basic annual', 59.99, 365, 1, date('2025-04-02'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Youtube', 'Student plan', 3.99, 30, 1, date('2026-04-02'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Youtube', 'Family plan', 19.99, 30, 1, date('2026-03-01'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Telegram', 'Pro annual', 199.99, 365, 1, date('2025-03-26'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Telegram', 'Trial 7 days', 0.00, 7, 1, date('2026-03-26'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Netflix', 'Monthly saver', 7.49, 30, 1, date('2026-03-15'));
                    INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Netflix', 'Enterprise', 999.99, 36500, 1, date('2026-03-24'));
                    """;
                stmt.executeUpdate(sampleData);
            }
        }
    }

    private static boolean isTableEmpty(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("cnt") == 0;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            initializeTables();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
