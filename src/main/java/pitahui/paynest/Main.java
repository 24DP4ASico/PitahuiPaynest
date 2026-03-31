package pitahui.paynest;

import lv.pitahui.paynest.db.DBConnection;
import lv.pitahui.paynest.db.UserDAO;
import lv.pitahui.paynest.db.SubscriptionDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        User u = new User(fn, ln, phone, iban);
        u.setPassword(pwd);
        u.save();
        System.out.println("\nAccount created for " + fn + " " + ln);
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
            System.out.println(" 3) Logout");
            System.out.print("\n> ");
            String c = scanner.nextLine().trim();
            if (c.equals("1")) {
                subscriptionsMenu(auth, scanner);
            } else if (c.equals("2")) {
                accountSettingsMenu(auth, scanner);
            } else if (c.equals("3")) {
                System.out.println("Logged out");
                break;
            } else {
                System.out.println("Unknown choice");
            }
        }
    }

    private static void subscriptionsMenu(User auth, Scanner scanner) throws SQLException {
        printSeparator();
        System.out.println("Subscriptions: 1=List 2=Create 3=Edit 4=Delete 5=Back");
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
            System.out.print("Duration (days): ");
            Integer dur = Integer.valueOf(scanner.nextLine().trim());
            System.out.print("Price (e.g. 12.34): ");
            Float price = Float.valueOf(scanner.nextLine().trim());
            Subscription sub = new Subscription(name, type, dur, price);
            sub.save();
            System.out.println("Subscription created: " + sub);
        } else if (s.equals("3")) {
            System.out.print("Subscription id to edit: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New name: ");
            String name = scanner.nextLine().trim();
            System.out.print("New type: ");
            String type = scanner.nextLine().trim();
            System.out.print("New duration: ");
            Integer dur = Integer.valueOf(scanner.nextLine().trim());
            System.out.print("New price: ");
            Float price = Float.valueOf(scanner.nextLine().trim());
            Subscription sub = new Subscription(name, type, dur, price);
            boolean ok = SubscriptionDAO.update(id, sub);
            System.out.println(ok ? "Subscription updated" : "Update failed");
        } else if (s.equals("4")) {
            System.out.print("Subscription id to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = SubscriptionDAO.deleteById(id);
            System.out.println(ok ? "Deleted" : "Delete failed");
        } else {
            // back
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
            System.out.print("New first name: ");
            String nf = scanner.nextLine().trim();
            System.out.print("New last name: ");
            String nl = scanner.nextLine().trim();
            System.out.print("New phone: ");
            String np = scanner.nextLine().trim();
            System.out.print("New IBAN: ");
            String ni = scanner.nextLine().trim();
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
