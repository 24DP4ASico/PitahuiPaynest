package pitahui.paynest;

/**
 * Apraksts (LV): Galvenā komandrindas lietotāja saskarne un programmas plūsma.
 * Description (EN): Main command-line UI and application flow.
 */

import lv.pitahui.paynest.db.DBConnection;
import lv.pitahui.paynest.db.UserDAO;
import lv.pitahui.paynest.db.SubscriptionDAO;
import lv.pitahui.paynest.db.BankAccountDAO;
import lv.pitahui.paynest.db.PaymentDAO;
import lv.pitahui.paynest.db.NotificationDAO;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import pitahui.paynest.Card;
import lv.pitahui.paynest.db.CardDAO;

public class Main {
    public static void main(String[] args) {
        clearScreen();
        printHeader("Pitahui Paynest - Terminal Interface");

        Scanner scanner = new Scanner(System.in);
        // generate notifications for subscriptions expiring today
        NotificationDAO.generateExpiryNotificationsForToday();
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
                // Create bank account for new user with zero initial balance
                BankAccountDAO.createAccount(created.getId(), 0.0);
                System.out.println("\nAccount created for " + fn + " " + ln);
                System.out.printf("Initial bank account balance: %.2f EUR\n", 0.0);
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
            boolean deleted = UserDAO.deleteByPhoneAndPassword(phone, pwd);
            System.out.println(deleted ? "\nAccount deleted" : "\nNo matching account");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void userMenu(User auth, Scanner scanner) throws SQLException {
        while (true) {
            printSeparator();
            System.out.println("User menu\n");
                System.out.println(" 1) Subscriptions");
                System.out.println(" 2) Account settings");
                System.out.println(" 3) Pay for subscription");
                System.out.println(" 4) Payment history");
                System.out.println(" 5) Notifications");
                System.out.println(" 6) Manage cards");
                System.out.println(" 7) Monthly total payments");
                System.out.println(" 8) Logout");
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
                // Notifications
                printSeparator();
                System.out.println("Notifications:\n");
                try {
                    var notes = lv.pitahui.paynest.db.NotificationDAO.getNotificationsByUserId(auth.getId());
                    if (notes.isEmpty()) {
                        System.out.println("No notifications.");
                    } else {
                        for (var n : notes) {
                            System.out.printf("id=%s, text=%s, date=%s, days_until=%s\n",
                                    n.get("id"), n.get("teksts"), n.get("izveidesDateums"), n.get("dienasLidзTerminam"));
                        }
                        System.out.print("Enter notification id to delete or press Enter to go back: ");
                        String del = scanner.nextLine().trim();
                        if (!del.isEmpty()) {
                            try {
                                int nid = Integer.parseInt(del);
                                boolean removed = lv.pitahui.paynest.db.NotificationDAO.deleteById(nid);
                                System.out.println(removed ? "Notification deleted" : "Delete failed");
                            } catch (NumberFormatException nfe) {
                                System.out.println("Invalid id");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error retrieving notifications: " + e.getMessage());
                }
            } else if (c.equals("6")) {
                manageCardsMenu(auth, scanner);
            } else if (c.equals("7")) {
                // Monthly total payments
                try {
                    java.time.LocalDate now = java.time.LocalDate.now();
                    int year = now.getYear();
                    int month = now.getMonthValue();
                    double total = lv.pitahui.paynest.db.PaymentSummaryDAO.calculateMonthlyTotal(auth.getId(), year, month);
                    System.out.printf("Total spent in %d-%02d: %.2f EUR\n", year, month, total);
                    // optionally store summary
                    String ym = String.format("%d-%02d", year, month);
                    try {
                        lv.pitahui.paynest.db.PaymentSummaryDAO.storeMonthlyTotal(auth.getId(), ym, total);
                    } catch (SQLException ignore) {}
                } catch (SQLException e) {
                    System.out.println("Error calculating monthly total: " + e.getMessage());
                }
            } else if (c.equals("8")) {
                System.out.println("Logged out");
                break;
            } else {
                System.out.println("Unknown choice");
            }
        }
    }

    private static void manageCardsMenu(User auth, Scanner scanner) throws SQLException {
        while (true) {
            printSeparator();
            System.out.println("Manage cards: 1=List 2=Add card 3=Delete 4=Back");
            System.out.print("\n> ");
            String a = scanner.nextLine().trim();
            if (a.equals("1")) {
                List<Card> cards = CardDAO.getByUserId(auth.getId());
                if (cards.isEmpty()) {
                    System.out.println("No saved cards.");
                } else {
                    for (Card c : cards) {
                        double bal = 0.0;
                        try {
                            var ba = lv.pitahui.paynest.db.BankAccountDAO.getByCardId(c.getId());
                            if (ba != null) bal = ba.getBilance();
                        } catch (SQLException ignore) {
                        }
                        System.out.printf("id=%d, card=%s, expiry=%s, name=%s, balance=%.2f EUR\n",
                                c.getId(), c.getMaskedNumber(), c.getExpiry(), c.getCardholderName(), bal);
                    }
                }
            } else if (a.equals("2")) {
                System.out.print("Card number (digits only): ");
                String num = scanner.nextLine().trim();
                System.out.print("Expiry (MM/YY): ");
                String exp = scanner.nextLine().trim();
                System.out.print("Cardholder name: ");
                String name = scanner.nextLine().trim();
                Card card = new Card(null, auth.getId(), num, exp, name);
                boolean ok = CardDAO.createCard(card);
                System.out.println(ok ? "Card saved" : "Failed to save card");
            } else if (a.equals("3")) {
                System.out.print("Enter card id to delete: ");
                try {
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    boolean ok = CardDAO.deleteById(id, auth.getId());
                    System.out.println(ok ? "Card deleted" : "Delete failed");
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid id");
                }
            } else {
                break;
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
            System.out.print("Duration (integer days): ");
            Integer dur = null;
            try {
                dur = Integer.valueOf(scanner.nextLine().trim());
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid duration — please enter an integer value.");
                return;
            }
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
                        System.out.print("New duration (integer days): ");
                        try {
                            Integer nd = Integer.valueOf(scanner.nextLine().trim());
                            current.subscriptionDuration(nd);
                            changed = true;
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid duration — must be an integer.");
                        }
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

        // Ask whether to use a saved card (if any) — card is informational only, actual debit uses bank account
        boolean usingCard = false;
        try {
            var cards = CardDAO.getByUserId(auth.getId());
            if (!cards.isEmpty()) {
                System.out.print("\nUse saved card to pay? (y/N): ");
                String use = scanner.nextLine().trim().toLowerCase();
                if (use.equals("y") || use.equals("yes")) {
                    usingCard = true;
                    System.out.println("Saved cards:");
                    for (var c : cards) {
                        System.out.printf("%d) %s %s %s\n", c.getId(), c.getMaskedNumber(), c.getExpiry(), c.getCardholderName());
                    }
                    System.out.print("Select card id to use: ");
                    String sel = scanner.nextLine().trim();
                    // selection is informational; not used for authorization here
                }
            }
        } catch (SQLException ignore) {
            // ignore, proceed without card
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
                try {
                    // set subscription activation date to today
                    boolean updated = SubscriptionDAO.updateActivationDate(subId, LocalDate.now().toString());
                    if (updated) {
                        try {
                            // remove stale notifications for this subscription
                            lv.pitahui.paynest.db.NotificationDAO.deleteByAbonementId(subId);
                        } catch (SQLException ignore) {
                            // non-fatal: continue to attempt to create fresh notification
                        }

                        try {
                            // recreate an up-to-date notification based on new activation date
                            Object durObj = selectedSub.get("duration");
                            Integer durationDays = durObj instanceof Number ? ((Number) durObj).intValue() : null;
                            lv.pitahui.paynest.db.NotificationDAO.createSubscriptionNotification(
                                    auth.getId(), subId, selectedSub.get("name").toString(), durationDays, LocalDate.now().toString());
                        } catch (SQLException ignore) {
                            // ignore creation failure (non-fatal)
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Warning: failed to update subscription activation date: " + e.getMessage());
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





