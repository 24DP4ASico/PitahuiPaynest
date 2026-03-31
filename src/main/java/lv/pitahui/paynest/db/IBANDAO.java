package lv.pitahui.paynest.db;

import pitahui.paynest.IBAN;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IBANDAO {

    // Insert a new IBAN account into the database
    public static void insert(IBAN iban) throws SQLException {
        String query = "INSERT INTO IBAN (Kontonumurs, KontoParadnieks, Banka_kods, Konta_numurs, BIC, Atlikums, Aktivs, Izstrade_datums) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, iban.getIbanNumber());
            stmt.setString(2, iban.getAccountHolder());
            stmt.setString(3, iban.getBankCode());
            stmt.setString(4, iban.getAccountNumber());
            stmt.setString(5, iban.getBic());
            stmt.setDouble(6, iban.getBalance());
            stmt.setBoolean(7, iban.isActive());
            stmt.executeUpdate();
        }
    }

    // Get IBAN account by ID
    public static IBAN getById(int id) throws SQLException {
        String query = "SELECT * FROM IBAN WHERE IBAN_ID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    IBAN iban = new IBAN(
                            rs.getString("Kontonumurs"),
                            rs.getString("KontoParadnieks"),
                            rs.getString("Banka_kods"),
                            rs.getString("Konta_numurs"),
                            rs.getString("BIC"),
                            rs.getDouble("Atlikums")
                    );
                    iban.setId(rs.getInt("IBAN_ID"));
                    iban.setActive(rs.getBoolean("Aktivs"));
                    return iban;
                }
            }
        }
        return null;
    }

    // Get IBAN account by IBAN number
    public static IBAN getByIBANNumber(String ibanNumber) throws SQLException {
        String query = "SELECT * FROM IBAN WHERE Kontonumurs = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ibanNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    IBAN iban = new IBAN(
                            rs.getString("Kontonumurs"),
                            rs.getString("KontoParadnieks"),
                            rs.getString("Banka_kods"),
                            rs.getString("Konta_numurs"),
                            rs.getString("BIC"),
                            rs.getDouble("Atlikums")
                    );
                    iban.setId(rs.getInt("IBAN_ID"));
                    iban.setActive(rs.getBoolean("Aktivs"));
                    return iban;
                }
            }
        }
        return null;
    }

    // Get all IBAN accounts
    public static List<IBAN> getAll() throws SQLException {
        String query = "SELECT * FROM IBAN WHERE Aktivs = true";
        List<IBAN> ibans = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                IBAN iban = new IBAN(
                        rs.getString("Kontonumurs"),
                        rs.getString("KontoParadnieks"),
                        rs.getString("Banka_kods"),
                        rs.getString("Konta_numurs"),
                        rs.getString("BIC"),
                        rs.getDouble("Atlikums")
                );
                iban.setId(rs.getInt("IBAN_ID"));
                iban.setActive(rs.getBoolean("Aktivs"));
                ibans.add(iban);
            }
        }
        return ibans;
    }

    // Update IBAN balance
    public static void updateBalance(int ibanId, double newBalance) throws SQLException {
        String query = "UPDATE IBAN SET Atlikums = ? WHERE IBAN_ID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, ibanId);
            stmt.executeUpdate();
        }
    }

    // Deduct balance for a payment
    public static void deductBalance(int ibanId, double amount) throws SQLException {
        String query = "UPDATE IBAN SET Atlikums = Atlikums - ? WHERE IBAN_ID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, ibanId);
            stmt.executeUpdate();
        }
    }

    // Add balance (refund)
    public static void addBalance(int ibanId, double amount) throws SQLException {
        String query = "UPDATE IBAN SET Atlikums = Atlikums + ? WHERE IBAN_ID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, ibanId);
            stmt.executeUpdate();
        }
    }

    // Delete IBAN account
    public static void delete(int id) throws SQLException {
        String query = "UPDATE IBAN SET Aktivs = false WHERE IBAN_ID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
