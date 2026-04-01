package lv.pitahui.paynest.db;

import lv.pitahui.paynest.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDAO {

    /**
     * Inserts a notification into the Pazinojums table
     */
    public static boolean insert(Notification notification) throws SQLException {
        String sql = "INSERT INTO Pazinojums (Lietotaja_ID, Abonementa_ID, Teksts, Izveides_datums, Dienas_lidz_terminam) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notification.getLietotajaId());
            pstmt.setInt(2, notification.getAbonementaId());
            pstmt.setString(3, notification.getTeksts());
            pstmt.setString(4, notification.getIzveidesDateums());
            pstmt.setInt(5, notification.getDienasLidзTerminam());

            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Retrieves all notifications for a specific user
     */
    public static List<Map<String, Object>> getNotificationsByUserId(Integer userId) throws SQLException {
        String sql = "SELECT Pazinojuma_ID, Lietotaja_ID, Abonementa_ID, Teksts, Izveides_datums, Dienas_lidz_terminam FROM Pazinojums WHERE Lietotaja_ID = ?";
        List<Map<String, Object>> notifications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("id", rs.getInt("Pazinojuma_ID"));
                    notification.put("lietotajaId", rs.getInt("Lietotaja_ID"));
                    notification.put("abonementaId", rs.getInt("Abonementa_ID"));
                    notification.put("teksts", rs.getString("Teksts"));
                    notification.put("izveidesDateums", rs.getString("Izveides_datums"));
                    notification.put("dienasLidзTerminam", rs.getInt("Dienas_lidz_terminam"));
                    notifications.add(notification);
                }
            }
        }
        return notifications;
    }

    /**
     * Calculates the number of days until subscription ending based on the subscription type
     * and activation date.
     *
     * Duration types:
     * - "Monthly" = 30 days
     * - "Annual" = 365 days
     * - "Trial" = 7 days
     * - "Permanent" = 100000000000000000000000000000000000000000000000 days (essentially infinite)
     *
     * @param durationType The subscription duration type
     * @param activationDate The subscription activation date (format: "yyyy-MM-dd")
     * @return Number of days until subscription ending
     */
    public static Integer calculateDaysUntilExpiry(String durationType, String activationDate) {
        if (durationType == null || activationDate == null) {
            return 0;
        }

        try {
            LocalDate activation = LocalDate.parse(activationDate);
            LocalDate today = LocalDate.now();
            long daysSinceActivation = java.time.temporal.ChronoUnit.DAYS.between(activation, today);

            int durationInDays = getDurationInDays(durationType);
            long daysUntilExpiry = durationInDays - daysSinceActivation;

            // Return 0 if already expired, otherwise return the calculated days
            return (int) Math.max(0, daysUntilExpiry);
        } catch (Exception e) {
            System.err.println("Error calculating days until expiry: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Converts duration type to days
     */
    private static int getDurationInDays(String durationType) {
        if (durationType == null) {
            return 0;
        }

        return switch (durationType.toLowerCase()) {
            case "monthly" -> 30;
            case "annual" -> 365;
            case "trial" -> 7;
            case "permanent" -> 100_000_000; // Treating permanent as effectively infinite
            default -> 0;
        };
    }

    /**
     * Creates a notification for a subscription with all details filled in
     */
    public static boolean createSubscriptionNotification(Integer lietotajaId, Integer abonementaId, 
                                                         String subscriptionName, String durationType, 
                                                         String activationDate) throws SQLException {
        // Calculate days until expiry
        Integer daysUntilExpiry = calculateDaysUntilExpiry(durationType, activationDate);

        // Create notification text
        String notificationText = String.format(
                "Your subscription '%s' is going to end soon, want to renew it?",
                subscriptionName
        );

        // Create notification object
        Notification notification = new Notification(
                lietotajaId,
                abonementaId,
                notificationText,
                LocalDate.now().toString(),
                daysUntilExpiry
        );

        // Insert into database
        return insert(notification);
    }

    /**
     * Retrieves all notifications (for admin view)
     */
    public static List<Map<String, Object>> getAllNotifications() throws SQLException {
        String sql = "SELECT Pazinojuma_ID, Lietotaja_ID, Abonementa_ID, Teksts, Izveides_datums, Dienas_lidz_terminam FROM Pazinojums";
        List<Map<String, Object>> notifications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("id", rs.getInt("Pazinojuma_ID"));
                    notification.put("lietotajaId", rs.getInt("Lietotaja_ID"));
                    notification.put("abonementaId", rs.getInt("Abonementa_ID"));
                    notification.put("teksts", rs.getString("Teksts"));
                    notification.put("izveidesDateums", rs.getString("Izveides_datums"));
                    notification.put("dienasLidзTerminam", rs.getInt("Dienas_lidz_terminam"));
                    notifications.add(notification);
                }
            }
        }
        return notifications;
    }

    /**
     * Deletes a notification by ID
     */
    public static boolean deleteById(Integer notificationId) throws SQLException {
        String sql = "DELETE FROM Pazinojums WHERE Pazinojuma_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }
}
