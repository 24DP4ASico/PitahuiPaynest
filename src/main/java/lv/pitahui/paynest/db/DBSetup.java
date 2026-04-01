package lv.pitahui.paynest.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DBSetup {

    public static void recreateTables() throws SQLException {
        String sql = """
            PRAGMA foreign_keys = OFF;
            Drop table if exists Lietotajs;
            Drop table if exists Abonements;
            Drop table if exists Maksajums;
            Drop table if exists Pazinojums;
            Drop table if exists Bankas_konts;

            PRAGMA foreign_keys = ON;

            CREATE TABLE Lietotajs (
                Lietotaja_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Vards TEXT(60),
                Uzvards TEXT(60),
                Talrunis TEXT(15) UNIQUE,
                IBAN TEXT(32) UNIQUE,
                Password TEXT(100)
            );

            CREATE TABLE Abonements (
                Abonementa_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nosaukums TEXT(200),
                Veids TEXT(100),
                Cena REAL(10, 2),
                Ilgums TEXT(20),
                Lietotaja_ID INTEGER,
                Aktivizacijas_datums Date,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            CREATE TABLE Maksajums (
                Maksajuma_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Abonementa_ID INTEGER,
                Lietotaja_ID INTEGER,
                Summa REAL(10, 2),
                Datums_un_Laiks DATETIME,
                Statuss TEXT(30),
                FOREIGN KEY (Abonementa_ID) REFERENCES Abonements(Abonementa_ID),
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            CREATE TABLE Pazinojums (
                Pazinojuma_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER,
                Abonementa_ID INTEGER,
                Teksts TEXT(300),
                Izveides_datums Date,
                Dienas_lidz_terminam INTEGER,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID),
                FOREIGN KEY (Abonementa_ID) REFERENCES Abonements(Abonementa_ID)
            );

            CREATE TABLE Bankas_konts (
                Bankas_konts_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER UNIQUE,
                Bilance REAL(10, 2),
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            -- sample user (owner for subscriptions)
            INSERT INTO Lietotajs (Vards, Uzvards, Talrunis, IBAN, Password) VALUES ('Admin', 'User', '+37100000001', 'LV00TEST000000000000', 'adminpw');

            -- sample bank account for admin user
            INSERT INTO Bankas_konts (Lietotaja_ID, Bilance) VALUES (1, 500.00);

            -- sample subscriptions (owned by the sample user id=1)
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Aktivizacijas_datums) VALUES ('Spotify', 'Basic monthly', 5.99, 'Monthly', date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Spotify', 'Basic monthly', 5.99, 'Monthly', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Kling.ai', 'Standard monthly', 9.99, 'Monthly', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Kling.ai', 'Premium monthly', 14.99, 'Monthly', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Spotify', 'Basic annual', 59.99, 'Annual', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Youtube', 'Student plan', 3.99, 'Monthly', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Youtube', 'Family plan', 19.99, 'Monthly', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Telegram', 'Pro annual', 199.99, 'Annual', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Telegram', 'Trial 7 days', 0.00, 'Trial', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Netflix', 'Monthly saver', 7.49, 'Monthly', 1, date('now'));
            INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES ('Netflix', 'Enterprise', 999.99, 'Annual', 1, date('now'));
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
        }
    }

    public static void main(String[] args) {
        try {
            recreateTables();
            System.out.println("Tables recreated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
