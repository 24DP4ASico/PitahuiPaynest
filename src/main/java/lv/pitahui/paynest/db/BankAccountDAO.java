package lv.pitahui.paynest.db;

import pitahui.paynest.BankAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BankAccountDAO {

    public static BankAccount getByUserId(Integer userId) throws SQLException {
        String sql = "SELECT Bankas_konts_ID, Lietotaja_ID, Bilance FROM Bankas_konts WHERE Lietotaja_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new BankAccount(
                            rs.getInt("Bankas_konts_ID"),
                            rs.getInt("Lietotaja_ID"),
                            rs.getDouble("Bilance")
                    );
                }
            }
        }
        return null;
    }

    public static boolean createAccount(Integer userId, double initialBalance) throws SQLException {
        String sql = "INSERT INTO Bankas_konts (Lietotaja_ID, Bilance) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDouble(2, initialBalance);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean updateBalance(Integer userId, double newBalance) throws SQLException {
        String sql = "UPDATE Bankas_konts SET Bilance = ? WHERE Lietotaja_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, userId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean hasEnoughBalance(Integer userId, double amount) throws SQLException {
        BankAccount account = getByUserId(userId);
        if (account == null) {
            return false;
        }
        return account.getBilance() >= amount;
    }
}
