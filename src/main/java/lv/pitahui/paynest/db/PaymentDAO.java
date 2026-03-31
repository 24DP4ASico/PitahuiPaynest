package lv.pitahui.paynest.db;

import pitahui.paynest.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAO {

    public static void insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO Maksajums (Abonementa_ID, Lietotaja_ID, Summa, Datums_un_Laiks, Statuss) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, payment.getSubscription().getSubscriptionId());
            pstmt.setInt(2, payment.getUser().getId());
            pstmt.setFloat(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentDate().toString());
            pstmt.setString(5, payment.getStatus());

            pstmt.executeUpdate();
        }
    }

    public static List<Map<String, Object>> getPaymentsByUserId(int userId) throws SQLException {
        String sql = "SELECT Maksajuma_ID, Abonementa_ID, Lietotaja_ID, Summa, Datums_un_Laiks, Statuss FROM Maksajums WHERE Lietotaja_ID = ?";
        List<Map<String, Object>> payments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> payment = new HashMap<>();
                    payment.put("id", rs.getInt("Maksajuma_ID"));
                    payment.put("subscriptionId", rs.getInt("Abonementa_ID"));
                    payment.put("userId", rs.getInt("Lietotaja_ID"));
                    payment.put("amount", rs.getFloat("Summa"));
                    payment.put("dateTime", rs.getString("Datums_un_Laiks"));
                    payment.put("status", rs.getString("Statuss"));
                    payments.add(payment);
                }
            }
        }
        return payments;
    }

    public static List<Map<String, Object>> getAllPayments() throws SQLException {
        String sql = "SELECT Maksajuma_ID, Abonementa_ID, Lietotaja_ID, Summa, Datums_un_Laiks, Statuss FROM Maksajums";
        List<Map<String, Object>> payments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> payment = new HashMap<>();
                payment.put("id", rs.getInt("Maksajuma_ID"));
                payment.put("subscriptionId", rs.getInt("Abonementa_ID"));
                payment.put("userId", rs.getInt("Lietotaja_ID"));
                payment.put("amount", rs.getFloat("Summa"));
                payment.put("dateTime", rs.getString("Datums_un_Laiks"));
                payment.put("status", rs.getString("Statuss"));
                payments.add(payment);
            }
        }
        return payments;
    }

    public static boolean deleteById(int paymentId) throws SQLException {
        String sql = "DELETE FROM Maksajums WHERE Maksajuma_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paymentId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean updateStatus(int paymentId, String status) throws SQLException {
        String sql = "UPDATE Maksajums SET Statuss = ? WHERE Maksajuma_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, paymentId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }
}
