package lv.pitahui.paynest.db;

/**
 * Apraksts (LV): `DBConnection` — uztur savienojumu ar SQLite datubāzi.
 * Description (EN): `DBConnection` — manages connection to the SQLite database.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:paynest.db";

    public static Connection getConnection() throws SQLException {
        // funkcija getConnection pieņem void tipa vērtību un atgriež Connection tipa vērtību (savienojums ar SQLite DB)
        return DriverManager.getConnection(DB_URL);
    }
}
