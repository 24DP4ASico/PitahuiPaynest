package lv.pitahui.paynest.db;

/**
 * Apraksts (LV): `UserDAO` — datu piekļuves slānis lietotāju CRUD operācijām.
 * Description (EN): `UserDAO` — data access layer for user CRUD operations.
 */

import pitahui.paynest.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public static void insert(User user) throws SQLException {
        String sql = "INSERT INTO Lietotajs (Vards, Uzvards, Talrunis, IBAN, Password, Language) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getPhonenum());
            pstmt.setString(4, user.getIBAN());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getLanguage());

            pstmt.executeUpdate();
        }
    }

    public static User authenticate(String phone, String password) throws SQLException {
        String sql = "SELECT Lietotaja_ID, Vards, Uzvards, Talrunis, IBAN, Password, Language FROM Lietotajs WHERE Talrunis = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, password);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getInt("Lietotaja_ID"), rs.getString("Vards"), rs.getString("Uzvards"), rs.getString("Talrunis"), rs.getString("IBAN"));
                    u.setPassword(rs.getString("Password"));
                    try { u.setLanguage(rs.getString("Language")); } catch (Exception ignore) {}
                    return u;
                }
            }
        }
        return null;
    }

    public static boolean deleteByPhoneAndPassword(String phone, String password) throws SQLException {
        String sql = "DELETE FROM Lietotajs WHERE Talrunis = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, password);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean changePassword(String phone, String oldPassword, String newPassword) throws SQLException {
        // Verify old password then update
        String verify = "SELECT Lietotaja_ID FROM Lietotajs WHERE Talrunis = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement vp = conn.prepareStatement(verify)) {

            vp.setString(1, phone);
            vp.setString(2, oldPassword);
            try (java.sql.ResultSet rs = vp.executeQuery()) {
                if (!rs.next()) return false;
            }
        }

        String upd = "UPDATE Lietotajs SET Password = ? WHERE Talrunis = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pu = conn.prepareStatement(upd)) {

            pu.setString(1, newPassword);
            pu.setString(2, phone);
            int affected = pu.executeUpdate();
            return affected > 0;
        }
    }

    public static User getByPhone(String phone) throws SQLException {
        String sql = "SELECT Lietotaja_ID, Vards, Uzvards, Talrunis, IBAN, Password, Language FROM Lietotajs WHERE Talrunis = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getInt("Lietotaja_ID"), rs.getString("Vards"), rs.getString("Uzvards"), rs.getString("Talrunis"), rs.getString("IBAN"));
                    u.setPassword(rs.getString("Password"));
                    try { u.setLanguage(rs.getString("Language")); } catch (Exception ignore) {}
                    return u;
                }
            }
        }
        return null;
    }

    public static boolean updateProfile(String phone, String oldPassword, String newFirst, String newLast, String newPhone, String newIban) throws SQLException {
        // Verify old password
        String verify = "SELECT Lietotaja_ID FROM Lietotajs WHERE Talrunis = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement vp = conn.prepareStatement(verify)) {

            vp.setString(1, phone);
            vp.setString(2, oldPassword);
            try (java.sql.ResultSet rs = vp.executeQuery()) {
                if (!rs.next()) return false;
            }
        }

        String upd = "UPDATE Lietotajs SET Vards = ?, Uzvards = ?, Talrunis = ?, IBAN = ? WHERE Talrunis = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pu = conn.prepareStatement(upd)) {

            pu.setString(1, newFirst);
            pu.setString(2, newLast);
            pu.setString(3, newPhone);
            pu.setString(4, newIban);
            pu.setString(5, phone);

            int affected = pu.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean updateLanguage(String phone, String newLanguage) throws SQLException {
        String verify = "SELECT Lietotaja_ID FROM Lietotajs WHERE Talrunis = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement vp = conn.prepareStatement(verify)) {

            vp.setString(1, phone);
            try (java.sql.ResultSet rs = vp.executeQuery()) {
                if (!rs.next()) return false;
            }
        }

        String upd = "UPDATE Lietotajs SET Language = ? WHERE Talrunis = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pu = conn.prepareStatement(upd)) {

            pu.setString(1, newLanguage);
            pu.setString(2, phone);
            int affected = pu.executeUpdate();
            return affected > 0;
        }
    }
}
