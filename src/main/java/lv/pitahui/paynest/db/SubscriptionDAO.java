package lv.pitahui.paynest.db;

import pitahui.paynest.Subscription;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionDAO {

    public static void insert(Subscription s) throws SQLException {
        String sql = "INSERT INTO Abonements (Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID, Aktivizacijas_datums) VALUES (?, ?, ?, ?, ?, ?)";

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
                pstmt.setString(4, s.getSubscriptionDuration());
            } else {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            }
            if (s.getLietotajaId() != null) {
                pstmt.setInt(5, s.getLietotajaId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            // Use current date as activation date when inserting
            pstmt.setString(6, LocalDate.now().toString());

            pstmt.executeUpdate();
        }
    }

    public static List<Map<String,Object>> listAll() throws SQLException {
        String sql = "SELECT Abonementa_ID, Nosaukums, Veids, Cena, Ilgums, Aktivizacijas_datums FROM Abonements";
        List<Map<String,Object>> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id", rs.getInt("Abonementa_ID"));
                m.put("name", rs.getString("Nosaukums"));
                m.put("type", rs.getString("Veids"));
                m.put("price", rs.getString("Cena"));
                m.put("duration", rs.getString("Ilgums"));
                m.put("activated", rs.getString("Aktivizacijas_datums"));
                out.add(m);
            }
        }
        return out;
    }

    public static boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Abonements WHERE Abonementa_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean update(int id, Subscription s) throws SQLException {
        String sql = "UPDATE Abonements SET Nosaukums = ?, Veids = ?, Cena = ?, Ilgums = ? WHERE Abonementa_ID = ?";
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
                pstmt.setString(4, s.getSubscriptionDuration());
            } else {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            }
            pstmt.setInt(5, id);

            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static pitahui.paynest.Subscription getById(int id) throws SQLException {
        String sql = "SELECT Nosaukums, Veids, Cena, Ilgums, Lietotaja_ID FROM Abonements WHERE Abonementa_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("Nosaukums");
                    String type = rs.getString("Veids");
                    Float price = rs.getObject("Cena") != null ? rs.getFloat("Cena") : null;
                    String duration = rs.getString("Ilgums");
                    Integer lietotajaId = rs.getObject("Lietotaja_ID") != null ? rs.getInt("Lietotaja_ID") : null;
                    return new pitahui.paynest.Subscription(name, type, duration, price, lietotajaId);
                }
            }
        }
        return null;
    }
}
