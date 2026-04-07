package lv.pitahui.paynest.db;

import pitahui.paynest.BankAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BankAccountDAO {

    public static BankAccount getByUserId(Integer userId) throws SQLException {
        String sql = "SELECT Bankas_konts_ID, Lietotaja_ID, Bilance, Kartes_ID FROM Bankas_konts WHERE Lietotaja_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Integer kartesId = rs.getObject("Kartes_ID") != null ? rs.getInt("Kartes_ID") : null;
                    return new BankAccount(
                            rs.getInt("Bankas_konts_ID"),
                            rs.getInt("Lietotaja_ID"),
                            rs.getDouble("Bilance"),
                            kartesId
                    );
                }
            }
        }
        return null;
    }

    public static boolean createAccount(Integer userId, double initialBalance) throws SQLException {
        return createAccountWithCard(userId, initialBalance, null);
    }

    public static boolean createAccountWithCard(Integer userId, double initialBalance, Integer kartesId) throws SQLException {
        String sql = "INSERT INTO Bankas_konts (Lietotaja_ID, Bilance, Kartes_ID) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDouble(2, initialBalance);
            if (kartesId == null) pstmt.setNull(3, java.sql.Types.INTEGER); else pstmt.setInt(3, kartesId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static BankAccount getByCardId(Integer kartesId) throws SQLException {
        String sql = "SELECT Bankas_konts_ID, Lietotaja_ID, Bilance, Kartes_ID FROM Bankas_konts WHERE Kartes_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, kartesId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Integer kId = rs.getObject("Kartes_ID") != null ? rs.getInt("Kartes_ID") : null;
                    return new BankAccount(
                            rs.getInt("Bankas_konts_ID"),
                            rs.getInt("Lietotaja_ID"),
                            rs.getDouble("Bilance"),
                            kId
                    );
                }
            }
        }
        return null;
    }

    public static boolean linkCardToUser(Integer userId, Integer kartesId) throws SQLException {
        String sql = "UPDATE Bankas_konts SET Kartes_ID = ? WHERE Lietotaja_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (kartesId == null) pstmt.setNull(1, java.sql.Types.INTEGER); else pstmt.setInt(1, kartesId);
            pstmt.setInt(2, userId);
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
