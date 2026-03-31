package pitahui.paynest;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class IBAN {
    private int id;
    private String ibanNumber;
    private String accountHolder;
    private String bankCode;
    private String accountNumber;
    private String bic;
    private double balance;
    private LocalDateTime createdDate;
    private boolean isActive;

    // IBAN regex pattern for basic validation (simplified for common European IBANs)
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$");

    public IBAN(String ibanNumber, String accountHolder, String bankCode, String accountNumber, String bic, double balance) {
        this.id = 0;
        this.ibanNumber = ibanNumber;
        this.accountHolder = accountHolder;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.bic = bic;
        this.balance = balance;
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
    }

    public IBAN(String ibanNumber, String accountHolder, double balance) {
        this.id = 0;
        this.ibanNumber = ibanNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
    }

    public IBAN(String ibanNumber, String accountHolder) {
        this.id = 0;
        this.ibanNumber = ibanNumber;
        this.accountHolder = accountHolder;
        this.balance = 0.0;
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
    }

    // Validate IBAN format
    public boolean validateIBAN() {
        return IBAN_PATTERN.matcher(ibanNumber).matches();
    }

    // Balance management methods
    public boolean hasEnoughBalance(double amount) {
        return balance >= amount;
    }

    public void deductBalance(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!hasEnoughBalance(amount)) {
            throw new IllegalArgumentException("Insufficient balance. Available: €" + balance + ", Required: €" + amount);
        }
        this.balance -= amount;
    }

    public void addBalance(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    // Get IBAN with masked digits for security
    public String getMaskedIBAN() {
        if (ibanNumber.length() < 8) {
            return "****";
        }
        String start = ibanNumber.substring(0, 4);
        String end = ibanNumber.substring(ibanNumber.length() - 4);
        return start + "..." + end;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getIbanNumber() {
        return ibanNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBic() {
        return bic;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setIbanNumber(String ibanNumber) {
        this.ibanNumber = ibanNumber;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "IBAN{" +
                "accountHolder='" + accountHolder + '\'' +
                ", ibanNumber=" + getMaskedIBAN() +
                ", balance=€" + balance +
                ", bankCode='" + bankCode + '\'' +
                ", isActive=" + isActive +
                ", createdDate=" + createdDate +
                '}';
    }
}
