package lv.pitahui.paynest.db;

import pitahui.paynest.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public static void insert(User user) throws SQLException {
        String sql = "INSERT INTO Lietotajs (Vards, Uzvards, Talrunis, IBAN, Password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getPhonenum());
            pstmt.setString(4, user.getIBAN());
            pstmt.setString(5, user.getPassword());

            pstmt.executeUpdate();
        }
    }

    public static User authenticate(String phone, String password) throws SQLException {
        String sql = "SELECT Lietotaja_ID, Vards, Uzvards, Talrunis, IBAN, Password FROM Lietotajs WHERE Talrunis = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, password);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getString("Vards"), rs.getString("Uzvards"), rs.getString("Talrunis"), rs.getString("IBAN"));
                    u.setPassword(rs.getString("Password"));
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
}
