package lv.pitahui.paynest.db;

/**
 * Apraksts (LV): `PaymentDAO` — pārvalda maksājumu rekordu CRUD un vaicājumus.
 * Description (EN): `PaymentDAO` — manages payment records CRUD and queries.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAO {

    public static boolean recordPayment(Integer abonementaId, Integer lietotajaId, double summa, String statuss) throws SQLException {
        String sql = "INSERT INTO Maksajums (Abonementa_ID, Lietotaja_ID, Summa, Datums_un_Laiks, Statuss) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            pstmt.setInt(1, abonementaId);
            pstmt.setInt(2, lietotajaId);
            pstmt.setDouble(3, summa);
            pstmt.setString(4, timestamp);
            pstmt.setString(5, statuss);

            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static List<Map<String, Object>> getPaymentHistoryByUserId(Integer userId) throws SQLException {
        String sql = """
                SELECT m.Maksajuma_ID, m.Abonementa_ID, a.Nosaukums, m.Summa, m.Datums_un_Laiks, m.Statuss
                FROM Maksajums m
                JOIN Abonements a ON m.Abonementa_ID = a.Abonementa_ID
                WHERE m.Lietotaja_ID = ?
                ORDER BY m.Datums_un_Laiks DESC
                """;
        List<Map<String, Object>> payments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> payment = new HashMap<>();
                    payment.put("id", rs.getInt("Maksajuma_ID"));
                    payment.put("subscriptionId", rs.getInt("Abonementa_ID"));
                    payment.put("subscriptionName", rs.getString("Nosaukums"));
                    payment.put("amount", rs.getDouble("Summa"));
                    payment.put("dateTime", rs.getString("Datums_un_Laiks"));
                    payment.put("status", rs.getString("Statuss"));
                    payments.add(payment);
                }
            }
        }
        return payments;
    }
}
