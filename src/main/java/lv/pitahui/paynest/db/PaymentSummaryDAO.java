package lv.pitahui.paynest.db;

/**
 * Apraksts (LV): `PaymentSummaryDAO` — kalkulē un saglabā mēneša kopējās izmaksas.
 * Description (EN): `PaymentSummaryDAO` — calculates and stores monthly payment totals.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentSummaryDAO {

    /**
     * funkcija calculateMonthlyTotal pieņem Integer tipa vērtību userId, int tipa vērtību year un int tipa vērtību month un atgriež double tipa vērtību total
     * Calculate total payments for a user for given year and month.
     * Month is 1-12.
     */
    public static double calculateMonthlyTotal(Integer userId, int year, int month) throws SQLException {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate next = start.plusMonths(1);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startStr = start.atStartOfDay().format(f);
        String endStr = next.atStartOfDay().format(f);

        // Count only successful payments
        String sql = "SELECT SUM(Summa) as total FROM Maksajums WHERE Lietotaja_ID = ? AND Datums_un_Laiks >= ? AND Datums_un_Laiks < ? AND Statuss = 'SUCCESS'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, startStr);
            pstmt.setString(3, endStr);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    /**
     * funkcija storeMonthlyTotal pieņem Integer tipa vērtību userId un String tipa vērtību monthYYYYMM un atgriež boolean tipa vērtību result
     */
    public static boolean storeMonthlyTotal(Integer userId, String monthYYYYMM, double total) throws SQLException {
        String sql = "INSERT INTO Kopejas_izmaksas (Lietotaja_ID, Menesis, Summa, Izveides_datums) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, monthYYYYMM);
            pstmt.setDouble(3, total);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }
}
