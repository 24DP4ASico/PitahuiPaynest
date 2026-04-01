package pitahui.paynest;

import lv.pitahui.paynest.db.DBConnection;
import lv.pitahui.paynest.db.UserDAO;
import lv.pitahui.paynest.db.SubscriptionDAO;
import lv.pitahui.paynest.db.BankAccountDAO;
import lv.pitahui.paynest.db.PaymentDAO;
import lv.pitahui.paynest.db.NotificationDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        clearScreen();
        printHeader("Pitahui Paynest - Terminal Interface");

        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                printSeparator();
                System.out.println("Main menu\n");
                System.out.println(" 1) Register");
                System.out.println(" 2) Login");
                System.out.println(" 3) Delete account");
                System.out.println(" 4) Exit");
                System.out.print("\n> ");
                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    registerFlow(scanner);
                } else if (choice.equals("2")) {
                    loginFlow(scanner);
                } else if (choice.equals("3")) {
                    deleteFlow(scanner);
                } else if (choice.equals("4")) {
                    System.out.println("Exiting. Goodbye.");
                    break;
                } else {
                    System.out.println("Unknown choice — please enter 1, 2, 3 or 4");
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static void registerFlow(Scanner scanner) {
        printSeparator();
        System.out.println("Create new account\n");
        System.out.print(" First name: ");
        String fn = scanner.nextLine().trim();
        System.out.print(" Last name : ");
        String ln = scanner.nextLine().trim();
        System.out.print(" Phone     : ");
        String phone = scanner.nextLine().trim();
        System.out.print(" IBAN      : ");
        String iban = scanner.nextLine().trim();
        System.out.print(" Password  : ");
        String pwd = scanner.nextLine();

        try {
            User u = new User(fn, ln, phone, iban);
            u.setPassword(pwd);
            u.save();
            
            // Get the newly created user's ID
            User created = UserDAO.authenticate(phone, pwd);
            if (created != null) {
                // Create bank account for new user with random balance between 100 and 2000
                double randomBalance = 100 + (Math.random() * 1900);
                BankAccountDAO.createAccount(created.getId(), randomBalance);
                System.out.println("\nAccount created for " + fn + " " + ln);
                System.out.printf("Initial bank account balance: %.2f EUR\n", randomBalance);
            }
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private static void loginFlow(Scanner scanner) {
        try {
            printSeparator();
            System.out.println("Login\n");
            System.out.print(" Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print(" Password: ");
            String pwd = scanner.nextLine();
            User auth = UserDAO.authenticate(phone, pwd);
            if (auth != null) {
                System.out.println("\nLogin success: " + auth.getName() + " " + auth.getSurname());
                userMenu(auth, scanner);
            } else {
                System.out.println("\nLogin failed: wrong phone or password");
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void deleteFlow(Scanner scanner) {
        try {
            printSeparator();
            System.out.println("Delete account\n");
            System.out.print(" Phone to delete: ");
            String phone = scanner.nextLine().trim();
            System.out.print(" Password: ");
            String pwd = scanner.nextLine();
            
            // First get the user to delete to obtain their ID
            User userToDelete = UserDAO.authenticate(phone, pwd);
            if (userToDelete != null) {
                // Delete bank account and payments first (cascade delete)
                try {
                    String deleteBankAccounts = "DELETE FROM Bankas_konts WHERE Lietotaja_ID = ?";
                    String deletePayments = "DELETE FROM Maksajums WHERE Lietotaja_ID = ?";
                    String deleteSubscriptions = "DELETE FROM Abonements WHERE Lietotaja_ID = ?";
                    
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement stmt1 = conn.prepareStatement(deletePayments);
                         PreparedStatement stmt2 = conn.prepareStatement(deleteBankAccounts);
                         PreparedStatement stmt3 = conn.prepareStatement(deleteSubscriptions)) {
                        stmt1.setInt(1, userToDelete.getId());
                        stmt2.setInt(1, userToDelete.getId());
                        stmt3.setInt(1, userToDelete.getId());
                        stmt1.executeUpdate();
                        stmt2.executeUpdate();
                        stmt3.executeUpdate();
                    }
                } catch (SQLException e) {
                    // If cascade deletion fails, continue to attempt user deletion
                }
            }
            
            boolean deleted = UserDAO.deleteByPhoneAndPassword(phone, pwd);
            System.out.println(deleted ? "\nAccount deleted" : "\nNo matching account");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void userMenu(User auth, Scanner scanner) throws SQLException {
        while (true) {
            try {
                // Get notification count (safe query, minimal database access)
                List<Map<String, Object>> userNotifications = NotificationDAO.getNotificationsByUserId(auth.getId());
                int notificationCount = userNotifications.size();
                
                printSeparator();
                System.out.println("User menu\n");
                System.out.println(" 1) Subscriptions");
                System.out.println(" 2) Account settings");
                System.out.println(" 3) Pay for subscription");
                System.out.println(" 4) Payment history");
                System.out.printf(" 5) Notifications (%d)\n", notificationCount);
                System.out.println(" 6) Logout");
                System.out.print("\n> ");
                String c = scanner.nextLine().trim();
                if (c.equals("1")) {
                    subscriptionsMenu(auth, scanner);
                } else if (c.equals("2")) {
                    accountSettingsMenu(auth, scanner);
                } else if (c.equals("3")) {
                    paymentFlow(auth, scanner);
                } else if (c.equals("4")) {
                    paymentHistoryMenu(auth, scanner);
                } else if (c.equals("5")) {
                    notificationsMenu(auth, scanner);
                } else if (c.equals("6")) {
                    System.out.println("Logged out");
                    break;
                } else {
                    System.out.println("Unknown choice");
                }
            } catch (SQLException e) {
                System.out.println("Error in user menu: " + e.getMessage());
            }
        }
    }

    private static void subscriptionsMenu(User auth, Scanner scanner) throws SQLException {
        printSeparator();
        System.out.println("Subscriptions: 1=List 2=Create 3=Edit 4=Delete 5=Filter 6=Find 7=Back");
        System.out.print("\n> ");
        String s = scanner.nextLine().trim();
        if (s.equals("1")) {
            List<Map<String, Object>> list = SubscriptionDAO.listAll();
            for (var m : list) {
                System.out.printf("id=%s, name=%s, type=%s, price=%s, duration=%s\n",
                        m.get("id"), m.get("name"), m.get("type"), m.get("price"), m.get("duration"));
            }
        } else if (s.equals("2")) {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Type: ");
            String type = scanner.nextLine().trim();
            System.out.print("Duration: ");
            String dur = scanner.nextLine().trim();
            System.out.print("Price (e.g. 12.34): ");
            Float price = Float.valueOf(scanner.nextLine().trim());
            Subscription sub = new Subscription(name, type, dur, price, auth.getId());
            sub.save();
            System.out.println("Subscription created: " + sub);
        } else if (s.equals("3")) {
            System.out.print("Subscription id to edit: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            Subscription current = SubscriptionDAO.getById(id);
            if (current == null) {
                System.out.println("No subscription found with id=" + id);
            } else {
                System.out.println("What do you want to edit? 1=Name 2=Type 3=Duration 4=Price 5=Back");
                System.out.print("\n> ");
                String f = scanner.nextLine().trim();
                boolean changed = false;
                switch (f) {
                    case "1":
                        System.out.print("New name: ");
                        String name = scanner.nextLine().trim();
                        current.subscriptionName(name);
                        changed = true;
                        break;
                    case "2":
                        System.out.print("New type: ");
                        String type = scanner.nextLine().trim();
                        current.subscriptionType(type);
                        changed = true;
                        break;
                    case "3":
                        System.out.print("New duration: ");
                        String nd = scanner.nextLine().trim();
                        current.subscriptionDuration(nd);
                        changed = true;
                        break;
                    case "4":
                        System.out.print("New price: ");
                        try {
                            Float price = Float.valueOf(scanner.nextLine().trim());
                            current.subscriptionPrice(price);
                            changed = true;
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid price");
                        }
                        break;
                    default:
                        // back or unknown
                        break;
                }
                if (changed) {
                    boolean ok = SubscriptionDAO.update(id, current);
                    System.out.println(ok ? "Subscription updated" : "Update failed");
                }
            }
        } else if (s.equals("4")) {
            System.out.print("Subscription id to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = SubscriptionDAO.deleteById(id);
            System.out.println(ok ? "Deleted" : "Delete failed");
        } else if (s.equals("5")) {
            System.out.println("Filter by: 1=Nosaukums(app name) 2=Veids(subscription name) 3=Ilgums(duration) 4=Back");
            System.out.print("\n> ");
            String f = scanner.nextLine().trim();
            if (f.equals("4")) {
                // back
            } else {
                System.out.print("Enter search value: ");
                String q = scanner.nextLine().trim().toLowerCase();
                List<Map<String, Object>> list = SubscriptionDAO.listAll();
                boolean any = false;
                for (var m : list) {
                    String fieldVal = "";
                    switch (f) {
                        case "1":
                            fieldVal = m.get("name") != null ? m.get("name").toString() : "";
                            break;
                        case "2":
                            fieldVal = m.get("type") != null ? m.get("type").toString() : "";
                            break;
                        case "3":
                            fieldVal = m.get("duration") != null ? m.get("duration").toString() : "";
                            break;
                        default:
                            fieldVal = "";
                    }
                    if (fieldVal.toLowerCase().contains(q)) {
                        System.out.printf("id=%s, name=%s, type=%s, price=%s, duration=%s\n",
                                m.get("id"), m.get("name"), m.get("type"), m.get("price"), m.get("duration"));
                        any = true;
                    }
                }
                if (!any) System.out.println("No matching subscriptions");
            }
        } else if (s.equals("6")) {
            System.out.print("Enter any value to search (name/type/duration/price/activated): ");
            String q = scanner.nextLine().trim().toLowerCase();
            List<Map<String, Object>> list = SubscriptionDAO.listAll();
            boolean any = false;
            for (var m : list) {
                String name = m.get("name") != null ? m.get("name").toString().toLowerCase() : "";
                String type = m.get("type") != null ? m.get("type").toString().toLowerCase() : "";
                String price = m.get("price") != null ? m.get("price").toString().toLowerCase() : "";
                String duration = m.get("duration") != null ? m.get("duration").toString().toLowerCase() : "";
                String activated = m.get("activated") != null ? m.get("activated").toString().toLowerCase() : "";
                if (name.contains(q) || type.contains(q) || price.contains(q) || duration.contains(q) || activated.contains(q)) {
                    System.out.printf("id=%s, name=%s, type=%s, price=%s, duration=%s, activated=%s\n",
                            m.get("id"), m.get("name"), m.get("type"), m.get("price"), m.get("duration"), m.get("activated"));
                    any = true;
                }
            }
            if (!any) System.out.println("No matching subscriptions");
        } else {
            // back
        }
    }

    private static void paymentFlow(User auth, Scanner scanner) throws SQLException {
        printSeparator();
        System.out.println("Pay for subscription\n");

        // Show only user's own subscriptions
        List<Map<String, Object>> userSubs = SubscriptionDAO.listAll();
        List<Map<String, Object>> filteredSubs = userSubs.stream()
                .filter(m -> auth.getId().equals(m.get("lietotaja_id")))
                .toList();

        if (filteredSubs.isEmpty()) {
            System.out.println("You have no subscriptions to pay for.");
            return;
        }

        System.out.println("Your subscriptions:");
        for (int i = 0; i < filteredSubs.size(); i++) {
            var m = filteredSubs.get(i);
            double priceValue = ((Number) Double.parseDouble(m.get("price").toString())).doubleValue();
            System.out.printf("%d) Name=%s, Type=%s, Price=%.2f EUR, Duration=%s\n",
                    i + 1, m.get("name"), m.get("type"), priceValue, m.get("duration"));
        }

        System.out.print("\nSelect subscription number to pay (or 0 to cancel): ");
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
            return;
        }

        if (choice == 0 || choice < 1 || choice > filteredSubs.size()) {
            System.out.println("Payment cancelled");
            return;
        }

        Map<String, Object> selectedSub = filteredSubs.get(choice - 1);
        double price = Double.parseDouble(selectedSub.get("price").toString());
        Integer subId = ((Number) selectedSub.get("id")).intValue();

        // Check balance
        BankAccount account = BankAccountDAO.getByUserId(auth.getId());
        if (account == null) {
            System.out.println("Error: Bank account not found.");
            return;
        }

        System.out.printf("\nPayment details:\n");
        System.out.printf("Subscription: %s\n", selectedSub.get("name"));
        System.out.printf("Amount: %.2f EUR\n", price);
        System.out.printf("Your balance: %.2f EUR\n", account.getBilance());

        if (account.getBilance() < price) {
            System.out.println("ERROR: Insufficient balance. Payment cancelled.");
            try {
                PaymentDAO.recordPayment(subId, auth.getId(), price, "FAILED - Insufficient balance");
            } catch (SQLException e) {
                System.out.println("Error recording failed payment: " + e.getMessage());
            }
            return;
        }

        // Confirm with password
        System.out.print("\nEnter your password to confirm payment: ");
        String pwd = scanner.nextLine();
        User verified = UserDAO.authenticate(auth.getPhonenum(), pwd);

        if (verified == null) {
            System.out.println("ERROR: Wrong password. Payment cancelled.");
            try {
                PaymentDAO.recordPayment(subId, auth.getId(), price, "FAILED - Wrong password");
            } catch (SQLException e) {
                System.out.println("Error recording failed payment: " + e.getMessage());
            }
            return;
        }

        // Process payment
        try {
            double newBalance = account.getBilance() - price;
            boolean balanceUpdated = BankAccountDAO.updateBalance(auth.getId(), newBalance);
            boolean paymentRecorded = PaymentDAO.recordPayment(subId, auth.getId(), price, "SUCCESS");

            if (balanceUpdated && paymentRecorded) {
                System.out.println("\n✓ Payment successful!");
                System.out.printf("Amount paid: %.2f EUR\n", price);
                System.out.printf("New balance: %.2f EUR\n", newBalance);
                
                // Generate notifications for paid subscriptions
                try {
                    generateNotifications(auth);
                } catch (Exception e) {
                    // Notification generation is not critical, don't fail payment on error
                }
            } else {
                System.out.println("\nERROR: Payment processing failed. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void paymentHistoryMenu(User auth, Scanner scanner) throws SQLException {
        printSeparator();
        System.out.println("Payment History\n");

        List<Map<String, Object>> payments = PaymentDAO.getPaymentHistoryByUserId(auth.getId());

        if (payments.isEmpty()) {
            System.out.println("You have no payment history.");
            return;
        }

        System.out.println("Your payments:\n");
        for (Map<String, Object> p : payments) {
            System.out.printf("Subscription: %s | Amount: %.2f EUR | Date: %s | Status: %s\n",
                    p.get("subscriptionName"),
                    ((Number) p.get("amount")).doubleValue(),
                    p.get("dateTime"),
                    p.get("status"));
        }
    }

    private static void accountSettingsMenu(User auth, Scanner scanner) throws SQLException {
        printSeparator();
        System.out.println("Account settings: 1=View 2=Edit account data 3=Change password 4=Delete account 5=Back");
        System.out.print("\n> ");
        String a = scanner.nextLine().trim();
        if (a.equals("1")) {
            User u = UserDAO.getByPhone(auth.getPhonenum());
            System.out.println(u != null ? u : "No account found");
        } else if (a.equals("2")) {
            System.out.print("Enter current password to confirm: ");
            String oldp = scanner.nextLine();
            User current = UserDAO.getByPhone(auth.getPhonenum());
            if (current == null) {
                System.out.println("Current user not found");
            } else {
                System.out.println("What do you want to edit? 1=First name 2=Last name 3=Phone 4=IBAN 5=Back");
                System.out.print("\n> ");
                String choice = scanner.nextLine().trim();
                String nf = current.getName();
                String nl = current.getSurname();
                String np = current.getPhonenum();
                String ni = current.getIBAN();
                boolean doUpdate = false;
                switch (choice) {
                    case "1":
                        System.out.print("New first name: ");
                        nf = scanner.nextLine().trim();
                        doUpdate = true;
                        break;
                    case "2":
                        System.out.print("New last name: ");
                        nl = scanner.nextLine().trim();
                        doUpdate = true;
                        break;
                    case "3":
                        System.out.print("New phone: ");
                        np = scanner.nextLine().trim();
                        doUpdate = true;
                        break;
                    case "4":
                        System.out.print("New IBAN: ");
                        ni = scanner.nextLine().trim();
                        doUpdate = true;
                        break;
                    default:
                        // back or unknown
                        break;
                }
                if (doUpdate) {
                    boolean ok = UserDAO.updateProfile(auth.getPhonenum(), oldp, nf, nl, np, ni);
                    System.out.println(ok ? "Profile updated" : "Profile update failed (wrong password?)");
                    if (ok) {
                        User updated = UserDAO.getByPhone(np);
                        if (updated != null) {
                            auth.setName(updated.getName());
                            auth.setSurname(updated.getSurname());
                            auth.setPhonenum(updated.getPhonenum());
                            auth.setIBAN(updated.getIBAN());
                        }
                    }
                }
            }
        } else if (a.equals("3")) {
            System.out.print("Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Old password: ");
            String oldp = scanner.nextLine();
            System.out.print("New password: ");
            String newp = scanner.nextLine();
            System.out.print("Confirm new password: ");
            String conf = scanner.nextLine();
            if (!newp.equals(conf)) {
                System.out.println("New password and confirmation do not match");
            } else {
                boolean ok = UserDAO.changePassword(phone, oldp, newp);
                System.out.println(ok ? "Password changed" : "Password change failed");
            }
        } else if (a.equals("4")) {
            System.out.print("Enter your password to confirm deletion: ");
            String pwd = scanner.nextLine();
            
            // Delete bank account, payments, and subscriptions first (cascade delete)
            try {
                String deleteBankAccounts = "DELETE FROM Bankas_konts WHERE Lietotaja_ID = ?";
                String deletePayments = "DELETE FROM Maksajums WHERE Lietotaja_ID = ?";
                String deleteSubscriptions = "DELETE FROM Abonements WHERE Lietotaja_ID = ?";
                
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt1 = conn.prepareStatement(deletePayments);
                     PreparedStatement stmt2 = conn.prepareStatement(deleteBankAccounts);
                     PreparedStatement stmt3 = conn.prepareStatement(deleteSubscriptions)) {
                    stmt1.setInt(1, auth.getId());
                    stmt2.setInt(1, auth.getId());
                    stmt3.setInt(1, auth.getId());
                    stmt1.executeUpdate();
                    stmt2.executeUpdate();
                    stmt3.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println("Error deleting related records: " + e.getMessage());
            }
            
            boolean deleted = UserDAO.deleteByPhoneAndPassword(auth.getPhonenum(), pwd);
            System.out.println(deleted ? "Account deleted" : "Delete failed");
            if (deleted) {
                System.out.println("You have been logged out (account deleted).");
                return; // return to main menu
            }
        } else {
            // back
        }
    }

    private static void notificationsMenu(User auth, Scanner scanner) throws SQLException {
        // Generate notifications when user opens notifications menu
        try {
            generateNotifications(auth);
        } catch (Exception e) {
            System.err.println("Warning: Could not generate fresh notifications: " + e.getMessage());
        }
        
        printSeparator();
        System.out.println("Notifications\n");

        List<Map<String, Object>> notifications = NotificationDAO.getNotificationsByUserId(auth.getId());

        if (notifications.isEmpty()) {
            System.out.println("You have no notifications.");
            return;
        }

        System.out.println("Your notifications:\n");
        for (int i = 0; i < notifications.size(); i++) {
            Map<String, Object> n = notifications.get(i);
            System.out.printf("%d) %s\n   Created: %s | Days remaining: %s\n",
                    i + 1,
                    n.get("teksts"),
                    n.get("izveidesDateums"),
                    n.get("dienasLidзTerminam"));
        }
        
        System.out.print("\nEnter notification number to delete (or 0 to go back): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice > 0 && choice <= notifications.size()) {
                Map<String, Object> selectedNotif = notifications.get(choice - 1);
                Integer notifId = ((Number) selectedNotif.get("id")).intValue();
                boolean deleted = NotificationDAO.deleteById(notifId);
                System.out.println(deleted ? "Notification deleted" : "Delete failed");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }

    private static void generateNotifications(User auth) throws SQLException {
        try {
            // Get all paid subscriptions for this user by checking payment history and subscription details
            String sql = """
                    SELECT DISTINCT a.Abonementa_ID, a.Nosaukums, a.Ilgums, a.Aktivizacijas_datums
                    FROM Abonements a
                    WHERE a.Lietotaja_ID = ? 
                    AND EXISTS (
                        SELECT 1 FROM Maksajums m 
                        WHERE m.Abonementa_ID = a.Abonementa_ID 
                        AND m.Lietotaja_ID = ? 
                        AND m.Statuss = 'SUCCESS'
                    )
                    """;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, auth.getId());
                pstmt.setInt(2, auth.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Integer abonementaId = rs.getInt("Abonementa_ID");
                        String subscriptionName = rs.getString("Nosaukums");
                        String durationType = rs.getString("Ilgums");
                        String activationDate = rs.getString("Aktivizacijas_datums");

                        // Calculate days until expiry
                        Integer daysUntilExpiry = NotificationDAO.calculateDaysUntilExpiry(durationType, activationDate);

                        // Check if notification should be created for this day threshold
                        int[] thresholds = {10, 7, 5, 2, 1};
                        for (int threshold : thresholds) {
                            if (daysUntilExpiry == threshold) {
                                // Check if notification already exists for this subscription at this threshold
                                if (!notificationExists(abonementaId, threshold)) {
                                    // Create the notification
                                    NotificationDAO.createSubscriptionNotification(
                                            auth.getId(),
                                            abonementaId,
                                            subscriptionName,
                                            durationType,
                                            activationDate
                                    );
                                }
                                break; // Only create one notification per subscription per day
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Log error but don't crash the menu - notifications are not critical
            System.err.println("Warning: Could not generate notifications: " + e.getMessage());
        }
    }

    private static boolean notificationExists(Integer abonementaId, Integer daysThreshold) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM Pazinojums WHERE Abonementa_ID = ? AND Dienas_lidz_terminam = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, abonementaId);
            pstmt.setInt(2, daysThreshold);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        }
        return false;
    }

    private static void printHeader(String title) {
        System.out.println("========================================");
        System.out.println(title);
        System.out.println("========================================");
    }

    private static void printSeparator() {
        System.out.println("----------------------------------------");
    }

    private static void clearScreen() {
        for (int i = 0; i < 30; i++) System.out.println();
    }
}
