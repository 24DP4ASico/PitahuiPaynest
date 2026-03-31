package pitahui.paynest;

import lv.pitahui.paynest.db.DBSetup;
import lv.pitahui.paynest.db.DBConnection;
import lv.pitahui.paynest.db.UserDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // ensure DB is available (do not recreate tables here to preserve data)

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nChoose: 1=Register 2=Login 3=Delete 4=Exit");
                String choice = scanner.nextLine().trim();
                if (choice.equals("1")) {
                    System.out.print("First name: ");
                    String fn = scanner.nextLine().trim();
                    System.out.print("Last name: ");
                    String ln = scanner.nextLine().trim();
                    System.out.print("Phone: ");
                    String phone = scanner.nextLine().trim();
                    System.out.print("IBAN: ");
                    String iban = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String pwd = scanner.nextLine();

                    User u = new User(fn, ln, phone, iban);
                    u.setPassword(pwd);
                    u.save();
                    System.out.println("Account created for " + fn + " " + ln);
                } else if (choice.equals("2")) {
                    System.out.print("Phone: ");
                    String phone = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String pwd = scanner.nextLine();
                    User auth = UserDAO.authenticate(phone, pwd);
                    if (auth != null) {
                        System.out.println("Login success: " + auth);
                        // enter user menu
                        userMenu(auth, scanner);
                    } else {
                        System.out.println("Login failed");
                    }
                } else if (choice.equals("3")) {
                    System.out.print("Phone to delete: ");
                    String phone = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String pwd = scanner.nextLine();
                    boolean deleted = UserDAO.deleteByPhoneAndPassword(phone, pwd);
                    System.out.println(deleted ? "Account deleted" : "No matching account");
                } else if (choice.equals("4")) {
                    System.out.println("Exiting");
                    break;
                } else {
                    System.out.println("Unknown choice");
                }
            }
            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void userMenu(User auth, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\nUser menu: 1=Subscriptions 2=Account settings 3=Logout");
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
        System.out.println("\nSubscriptions: 1=List 2=Create 3=Edit 4=Delete 5=Back");
        String s = scanner.nextLine().trim();
        if (s.equals("1")) {
            java.util.List<java.util.Map<String,Object>> list = lv.pitahui.paynest.db.SubscriptionDAO.listAll();
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
            boolean ok = lv.pitahui.paynest.db.SubscriptionDAO.update(id, sub);
            System.out.println(ok ? "Subscription updated" : "Update failed");
        } else if (s.equals("4")) {
            System.out.print("Subscription id to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = lv.pitahui.paynest.db.SubscriptionDAO.deleteById(id);
            System.out.println(ok ? "Deleted" : "Delete failed");
        } else {
            // back
        }
    }

    private static void accountSettingsMenu(User auth, Scanner scanner) throws SQLException {
        System.out.println("\nAccount settings: 1=View 2=Edit account data 3=Change password 4=Delete account 5=Back");
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
                // refresh auth object
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
            if (deleted) throw new SQLException("AccountDeleted");
        } else {
            // back
        }
    }
}
