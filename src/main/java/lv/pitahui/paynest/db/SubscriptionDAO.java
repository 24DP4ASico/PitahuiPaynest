package lv.pitahui.paynest.db;

import pitahui.paynest.Subscription;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class SubscriptionDAO {

    public static void insert(Subscription s) throws SQLException {
        String sql = "INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Aktivizacijas_datums) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getSubscriptionName());
            pstmt.setString(2, s.getSubscriptionType());
            if (s.getSubscriptionPrice() != null) {
                pstmt.setFloat(3, s.getSubscriptionPrice());
            } else {
                pstmt.setNull(3, java.sql.Types.REAL);
            }
            if (s.getSubscriptionDuration() != null) {
                pstmt.setInt(4, s.getSubscriptionDuration());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }

            // Use current date as activation date when inserting
            pstmt.setString(5, LocalDate.now().toString());

            pstmt.executeUpdate();
        }
    }
}
