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
            // recreate tables
            DBSetup.recreateTables();
            System.out.println("Tables recreated.");

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

            // print database rows for verification
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement()) {

                System.out.println("\n--- Lietotajs table ---");
                try (ResultSet rs = st.executeQuery("SELECT Lietotaja_ID, Vards, Uzvards, Talrunis, IBAN FROM Lietotajs")) {
                    while (rs.next()) {
                        System.out.printf("id=%d, name=%s %s, phone=%s, iban=%s\n",
                                rs.getInt("Lietotaja_ID"), rs.getString("Vards"), rs.getString("Uzvards"), rs.getString("Talrunis"), rs.getString("IBAN"));
                    }
                }

                System.out.println("\n--- Abonements table ---");
                try (ResultSet rs2 = st.executeQuery("SELECT Abonementa_ID, Nosaukums, Veids, Cena, Ilgums, Aktivizacijas_datums FROM Abonements")) {
                    while (rs2.next()) {
                        System.out.printf("id=%d, name=%s, type=%s, price=%s, duration=%s, activated=%s\n",
                                rs2.getInt("Abonementa_ID"), rs2.getString("Nosaukums"), rs2.getString("Veids"), rs2.getString("Cena"), rs2.getObject("Ilgums"), rs2.getString("Aktivizacijas_datums"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
