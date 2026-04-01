package lv.pitahui.paynest.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}
