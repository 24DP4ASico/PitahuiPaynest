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


    public static Integer calculateDaysUntilExpiry(Integer durationDays, String activationDate) {
        if (durationDays == null || activationDate == null) {
            return 0;
        }

        try {
            LocalDate activation = LocalDate.parse(activationDate);
            LocalDate today = LocalDate.now();
            long daysSinceActivation = java.time.temporal.ChronoUnit.DAYS.between(activation, today);

            long daysUntilExpiry = durationDays - daysSinceActivation;

            return (int) Math.max(0, daysUntilExpiry);
        } catch (Exception e) {
            System.err.println("Error calculating days until expiry: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Creates a notification for a subscription with all details filled in
     */
    public static boolean createSubscriptionNotification(Integer lietotajaId, Integer abonementaId,
                                                         String subscriptionName, Integer durationDays,
                                                         String activationDate) throws SQLException {
        // Calculate days until expiry
        Integer daysUntilExpiry = calculateDaysUntilExpiry(durationDays, activationDate);

        if (daysUntilExpiry == null) daysUntilExpiry = 0;

        // Create notification text
        String notificationText = String.format(
                "Your subscription '%s' is going to end today — consider renewing.",
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
     * Scan subscriptions and create notifications for those expiring today.
     */
    public static void generateExpiryNotificationsForToday() {
        try {
            var subs = SubscriptionDAO.listAll();
            for (var s : subs) {
                Object durObj = s.get("duration");
                Integer durationDays = durObj instanceof Number ? ((Number) durObj).intValue() : null;
                String activation = s.get("activated") != null ? s.get("activated").toString() : null;
                Integer userId = s.get("lietotaja_id") instanceof Number ? ((Number) s.get("lietotaja_id")).intValue() : null;
                Integer subId = s.get("id") instanceof Number ? ((Number) s.get("id")).intValue() : null;
                String name = s.get("name") != null ? s.get("name").toString() : "";

                if (durationDays == null || activation == null || subId == null || userId == null) continue;

                Integer daysUntilExpiry = calculateDaysUntilExpiry(durationDays, activation);
                if (daysUntilExpiry != null && daysUntilExpiry == 0) {
                    // Create notification if not already present (simple insert)
                    createSubscriptionNotification(userId, subId, name, durationDays, activation);
                }
            }
        } catch (Exception e) {
            System.err.println("Error generating expiry notifications: " + e.getMessage());
        }
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
