package lv.pitahui.paynest.db;

import pitahui.paynest.Card;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public static boolean createCard(Card card) throws SQLException {
        String sql = "INSERT INTO Kartes (Lietotaja_ID, Kartes_numurs, Derigums, Kartes_vards) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, card.getUserId());
            pstmt.setString(2, card.getCardNumber());
            pstmt.setString(3, card.getExpiry());
            pstmt.setString(4, card.getCardholderName());
            int affected = pstmt.executeUpdate();
            if (affected <= 0) return false;
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int newId = gk.getInt(1);
                    // link newly created card to user's bank account (if any)
                    try {
                        BankAccountDAO.linkCardToUser(card.getUserId(), newId);
                    } catch (SQLException ignore) {
                        // linking failed - still return success for card creation
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public static List<Card> getByUserId(Integer userId) throws SQLException {
        String sql = "SELECT Karte_ID, Lietotaja_ID, Kartes_numurs, Derigums, Kartes_vards FROM Kartes WHERE Lietotaja_ID = ?";
        List<Card> cards = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(new Card(
                            rs.getInt("Karte_ID"),
                            rs.getInt("Lietotaja_ID"),
                            rs.getString("Kartes_numurs"),
                            rs.getString("Derigums"),
                            rs.getString("Kartes_vards")
                    ));
                }
            }
        }
        return cards;
    }

    public static boolean deleteById(int cardId, int userId) throws SQLException {
        String sql = "DELETE FROM Kartes WHERE Karte_ID = ? AND Lietotaja_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cardId);
            pstmt.setInt(2, userId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }
}
