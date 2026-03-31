package pitahui.paynest;

import lv.pitahui.paynest.db.DBSetup;
import lv.pitahui.paynest.db.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            // recreate tables
            DBSetup.recreateTables();
            System.out.println("Tables recreated.");

            // create and save a user
            User user1 = new User("John", "Doe", "1234567890", "TR123456789012345678901234");
            user1.save();
            System.out.println("Saved user: " + user1);

            // create subscription with null price, then set from string to enforce two decimals
            Subscription subscription1 = new Subscription("Netflix", "Entertainment", 12, 15.99f);
            subscription1.save();
            System.out.println("Saved subscription: " + subscription1);

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
