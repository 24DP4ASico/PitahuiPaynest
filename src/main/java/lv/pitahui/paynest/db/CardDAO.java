package lv.pitahui.paynest.db;

import pitahui.paynest.Card;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
                    // create a bank account for this card (test random initial balance)
                    int randomInitial = ThreadLocalRandom.current().nextInt(100, 3001);
                    try {
                        BankAccountDAO.createAccountWithCard(card.getUserId(), (double) randomInitial, newId);
                    } catch (SQLException ignore) {
                        // ignore; card was created but bank account creation failed
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
