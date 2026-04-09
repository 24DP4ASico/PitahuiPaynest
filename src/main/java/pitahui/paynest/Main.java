package pitahui.paynest;

/**
 * Apraksts (LV): Galvenā komandrindas lietotāja saskarne un programmas plūsma.
 */

import java.io.Console;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import lv.pitahui.paynest.db.BankAccountDAO;
import lv.pitahui.paynest.db.CardDAO;
import lv.pitahui.paynest.db.NotificationDAO;
import lv.pitahui.paynest.db.PaymentDAO;
import lv.pitahui.paynest.db.SubscriptionDAO;
import lv.pitahui.paynest.db.UserDAO;

public class Main {
    // ANSI escape codes for terminal colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        // funkcija main pieņem String[] tipa vērtību args un atgriež void tipa vērtību
        clearScreen();
        printHeader(Messages.t("app.title"));

        Scanner scanner = new Scanner(System.in);
        // generate notifications for subscriptions expiring today
        NotificationDAO.generateExpiryNotificationsForToday();
        try {
            while (true) {
                printSeparator();
                System.out.println(colorText(Messages.t("menu.main.title"), CYAN));
                System.out.println(colorText(Messages.t("menu.main.register"), GREEN));
                System.out.println(colorText(Messages.t("menu.main.login"), GREEN));
                System.out.println(colorText(Messages.t("menu.main.delete"), GREEN));
                System.out.println(colorText(Messages.t("menu.main.exit"), GREEN));
                System.out.print(colorText("\n> ", MAGENTA));
                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    registerFlow(scanner);
                } else if (choice.equals("2")) {
                    loginFlow(scanner);
                } else if (choice.equals("3")) {
                    deleteFlow(scanner);
                } else if (choice.equals("4")) {
                    System.out.println(Messages.t("exiting"));
                    break;
                } else {
                    System.out.println(Messages.t("unknown.choice"));
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static void registerFlow(Scanner scanner) {
        // funkcija registerFlow pieņem Scanner tipa vērtību scanner un atgriež void tipa vērtību
        printSeparator();
        System.out.println(colorText(Messages.t("create.title"), BLUE));
        System.out.print(colorText(" First name: ", YELLOW));
        String fn = scanner.nextLine().trim();
        System.out.print(colorText(" Last name : ", YELLOW));
        String ln = scanner.nextLine().trim();
        System.out.print(colorText(" Phone     : ", YELLOW));
        String phone = scanner.nextLine().trim();
        System.out.print(colorText(" IBAN      : ", YELLOW));
        String iban = scanner.nextLine().trim();
        String pwd = readPassword(scanner, " Password  : ");

        try {
            User u = new User(fn, ln, phone, iban);
            u.setPassword(pwd);
            u.save();
            
            // Get the newly created user's ID
            User created = UserDAO.authenticate(phone, pwd);
            if (created != null) {
                // Create bank account for new user with zero initial balance
                BankAccountDAO.createAccount(created.getId(), 0.0);
                System.out.println(Messages.t("account.created") + fn + " " + ln);
                System.out.printf(Messages.t("initial.balance") + "%.2f EUR\n", 0.0);
            }
        } catch (SQLException e) {
            System.out.println(Messages.t("error.create") + e.getMessage());
        }
    }

    private static void loginFlow(Scanner scanner) {
        // funkcija loginFlow pieņem Scanner tipa vērtību scanner un atgriež void tipa vērtību
        try {
            printSeparator();
            System.out.println(colorText(Messages.t("login.title"), BLUE));
            System.out.print(colorText(" Phone: ", YELLOW));
            String phone = scanner.nextLine().trim();
            String pwd = readPassword(scanner, " Password: ");
            User auth = UserDAO.authenticate(phone, pwd);
            if (auth != null) {
                System.out.println(Messages.t("login.success") + auth.getName() + " " + auth.getSurname());
                // apply user's language to messages
                Messages.setLanguage(auth.getLanguage());
                userMenu(auth, scanner);
            } else {
                System.out.println(Messages.t("login.failed"));
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void deleteFlow(Scanner scanner) {
        // funkcija deleteFlow pieņem Scanner tipa vērtību scanner un atgriež void tipa vērtību
        try {
            printSeparator();
            System.out.println(colorText(Messages.t("delete.title"), BLUE));
            System.out.print(colorText(" Phone to delete: ", YELLOW));
            String phone = scanner.nextLine().trim();
            String pwd = readPassword(scanner, " Password: ");
            boolean deleted = UserDAO.deleteByPhoneAndPassword(phone, pwd);
            System.out.println(deleted ? Messages.t("account.deleted") : Messages.t("no.match"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void userMenu(User auth, Scanner scanner) throws SQLException {
        // funkcija userMenu pieņem User tipa vērtību auth un Scanner tipa vērtību scanner un atgriež void tipa vērtību
        while (true) {
            printSeparator();
            System.out.println(colorText(Messages.t("user.menu.title"), MAGENTA));
                System.out.println(colorText(Messages.t("user.menu.subs"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.settings"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.pay"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.history"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.notifications"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.managecards"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.monthly"), CYAN));
                System.out.println(colorText(Messages.t("user.menu.logout"), CYAN));
            System.out.print(colorText("\n> ", MAGENTA));
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
                System.out.println(Messages.t("logged.out"));
                break;
            } else {
                System.out.println(Messages.t("unknown.choice"));
            }
        }
    }

    private static void manageCardsMenu(User auth, Scanner scanner) throws SQLException {
        // funkcija manageCardsMenu pieņem User tipa vērtību auth un Scanner tipa vērtību scanner un atgriež void tipa vērtību
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
        // funkcija subscriptionsMenu pieņem User tipa vērtību auth un Scanner tipa vērtību scanner un atgriež void tipa vērtību
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
        // funkcija paymentFlow pieņem User tipa vērtību auth un Scanner tipa vērtību scanner un atgriež void tipa vērtību
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
        String pwd = readPassword(scanner, "\nEnter your password to confirm payment: ");
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
        // funkcija paymentHistoryMenu pieņem User tipa vērtību auth un Scanner tipa vērtību scanner un atgriež void tipa vērtību
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
        // funkcija accountSettingsMenu pieņem User tipa vērtību auth un Scanner tipa vērtību scanner un atgriež void tipa vērtību
        printSeparator();
        System.out.println("Account settings: 1=View 2=Edit account data 3=Change password 4=Delete account 5=Change language 6=Back");
        System.out.print("\n> ");
        String a = scanner.nextLine().trim();
        if (a.equals("1")) {
            User u = UserDAO.getByPhone(auth.getPhonenum());
            System.out.println(u != null ? u : "No account found");
        } else if (a.equals("2")) {
            String oldp = readPassword(scanner, "Enter current password to confirm: ");
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
            String oldp = readPassword(scanner, "Old password: ");
            String newp = readPassword(scanner, "New password: ");
            String conf = readPassword(scanner, "Confirm new password: ");
            if (!newp.equals(conf)) {
                System.out.println("New password and confirmation do not match");
            } else {
                boolean ok = UserDAO.changePassword(phone, oldp, newp);
                System.out.println(ok ? "Password changed" : "Password change failed");
            }
        } else if (a.equals("4")) {
            String pwd = readPassword(scanner, "Enter your password to confirm deletion: ");
            boolean deleted = UserDAO.deleteByPhoneAndPassword(auth.getPhonenum(), pwd);
            System.out.println(deleted ? "Account deleted" : "Delete failed");
            if (deleted) {
                System.out.println("You have been logged out (account deleted).");
                return; // return to main menu
            }
        } else if (a.equals("5")) {
            // Change language
            String current = auth.getLanguage() != null ? auth.getLanguage() : "LV";
            String currentName = switch (current.toUpperCase()) {
                case "EN" -> "English";
                case "RU" -> "Russian";
                default -> "Latvian";
            };
            System.out.println("Current language: " + currentName + " (" + current + ")");
            System.out.println("Choose language: 1=English 2=Russian 3=Latvian 4=Cancel");
            System.out.print("\n> ");
            String langChoice = scanner.nextLine().trim();
            String code = null;
            switch (langChoice) {
                case "1": code = "EN"; break;
                case "2": code = "RU"; break;
                case "3": code = "LV"; break;
                default: System.out.println("Language change cancelled"); break;
            }
            if (code != null) {
                try {
                    boolean ok = UserDAO.updateLanguage(auth.getPhonenum(), code);
                    System.out.println(ok ? "Language updated to " + code : "Failed to update language");
                    if (ok) {
                        auth.setLanguage(code);
                        Messages.setLanguage(code);
                    }
                } catch (SQLException e) {
                    System.out.println("Error updating language: " + e.getMessage());
                }
            }
        } else {
            // back
        }
        }
    

    private static String colorText(String text, String color) {
        // funkcija colorText pieņem tekstu un krāsu kodu, atgriež formatēta ANSI teksta virkni
        return color + text + RESET;
    }

    private static String readPassword(Scanner scanner, String prompt) {
        // funkcija readPassword nolasa paroli no termināļa un neizdrukā tās tiešo tekstu
        Console console = System.console();
        if (console != null) {
            char[] pwdChars = console.readPassword(colorText(prompt, YELLOW));
            if (pwdChars == null) return "";
            String pwd = new String(pwdChars);
            java.util.Arrays.fill(pwdChars, ' ');
            return pwd;
        }
        System.out.print(colorText(prompt, YELLOW));
        String pwd = scanner.nextLine();
        System.out.println(colorText("****", YELLOW));
        return pwd;
    }

    private static void printHeader(String title) {
        // funkcija printHeader pieņem String tipa vērtību title un atgriež void tipa vērtību
        System.out.println(colorText("========================================", BLUE));
        System.out.println(colorText(title, CYAN));
        System.out.println(colorText("========================================", BLUE));
    }

    private static void printSeparator() {
        // funkcija printSeparator pieņem void tipa vērtību un atgriež void tipa vērtību
        System.out.println(colorText("----------------------------------------", YELLOW));
    }

    private static void clearScreen() {
        // funkcija clearScreen pieņem void tipa vērtību un atgriež void tipa vērtību
        for (int i = 0; i < 30; i++) System.out.println();
    }
}





