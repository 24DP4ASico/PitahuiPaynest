package pitahui.paynest;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
    private String paymentId;
    private User user;
    private Subscription subscription;
    private String paymentMethod; // "IBAN", "CARD", "OTHER"
    private Float amount;
    private String status; // "PENDING", "COMPLETED", "FAILED", "CANCELLED"
    private LocalDateTime paymentDate;
    private LocalDateTime completedDate;
    private String transactionReference;
    private String notes;

    public Payment(User user, Subscription subscription, String paymentMethod, Float amount) {
        this.paymentId = UUID.randomUUID().toString();
        this.user = user;
        this.subscription = subscription;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = "PENDING";
        this.paymentDate = LocalDateTime.now();
    }

    // Process payment
    public boolean processPayment() {
        try {
            // Validate payment details
            if (user == null || subscription == null || amount <= 0) {
                this.status = "FAILED";
                this.notes = "Invalid payment details";
                return false;
            }

            // Validate IBAN if payment method is IBAN
            if ("IBAN".equalsIgnoreCase(paymentMethod)) {
                IBAN userIBAN = new IBAN(user.getIBAN(), user.getName() + " " + user.getSurname());
                if (!userIBAN.validateIBAN()) {
                    this.status = "FAILED";
                    this.notes = "Invalid IBAN format";
                    return false;
                }
            }

            // Simulate payment processing
            this.transactionReference = "TRX-" + System.currentTimeMillis();
            this.status = "COMPLETED";
            this.completedDate = LocalDateTime.now();
            return true;
        } catch (Exception e) {
            this.status = "FAILED";
            this.notes = "Payment processing error: " + e.getMessage();
            return false;
        }
    }

    // Getters
    public String getPaymentId() {
        return paymentId;
    }

    public User getUser() {
        return user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Float getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Cancel payment
    public void cancelPayment() {
        this.status = "CANCELLED";
        this.completedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", user=" + user.getName() + " " + user.getSurname() +
                ", subscription='" + subscription.getSubscriptionName() + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", paymentDate=" + paymentDate +
                ", transactionReference='" + transactionReference + '\'' +
                '}';
    }
}
