package lv.pitahui.paynest.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DBSetup {

    public static void recreateTables() throws SQLException {
        String sql = """
            PRAGMA foreign_keys = OFF;

            DROP TABLE IF EXISTS Maksajums;
            DROP TABLE IF EXISTS Pazinojums;
            DROP TABLE IF EXISTS Lietotajs;
            DROP TABLE IF EXISTS Abonements;

            PRAGMA foreign_keys = ON;

            CREATE TABLE Lietotajs (
                Lietotaja_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Vards TEXT(60),
                Uzvards TEXT(60),
                Talrunis TEXT(15),
                IBAN TEXT(32)
            );

            CREATE TABLE Abonements (
                Abonementa_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nosaukums TEXT(200),
                Veids TEXT(100),
                Cena REAL,
                Ilgums INTEGER,
                Aktivizacijas_datums TEXT
            );

            CREATE TABLE Maksajums (
                Maksajuma_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Abonementa_ID INTEGER,
                Lietotaja_ID INTEGER,
                Summa REAL,
                Datums_un_Laiks TEXT,
                Statuss TEXT(30),
                FOREIGN KEY (Abonementa_ID) REFERENCES Abonements(Abonementa_ID),
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID)
            );

            CREATE TABLE Pazinojums (
                Pazinojuma_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Lietotaja_ID INTEGER,
                Abonementa_ID INTEGER,
                Teksts TEXT(300),
                Izveides_datums TEXT,
                Dienas_lidz_terminam INTEGER,
                FOREIGN KEY (Lietotaja_ID) REFERENCES Lietotajs(Lietotaja_ID),
                FOREIGN KEY (Abonementa_ID) REFERENCES Abonements(Abonementa_ID)
            );
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
