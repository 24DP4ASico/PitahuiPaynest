package lv.pitahui.paynest.db;

import pitahui.paynest.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public static void insert(User user) throws SQLException {
        String sql = "INSERT INTO Lietotajs (Vards, Uzvards, Talrunis, IBAN) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getPhonenum());
            pstmt.setString(4, user.getIBAN());

            pstmt.executeUpdate();
        }
    }
}
