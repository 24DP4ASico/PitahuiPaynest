package pitahui.paynest;

public class Card {
    private Integer id;
    private Integer userId;
    private String cardNumber;
    private String expiry;
    private String cardholderName;

    public Card(Integer id, Integer userId, String cardNumber, String expiry, String cardholderName) {
        this.id = id;
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cardholderName = cardholderName;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public String getMaskedNumber() {
        if (cardNumber == null) return "";
        String s = cardNumber.replaceAll("\\s+", "");
        if (s.length() <= 4) return s;
        String last4 = s.substring(s.length() - 4);
        return "**** **** **** " + last4;
    }

    @Override
    public String toString() {
        return "Card{id=" + id + ", userId=" + userId + ", card=" + getMaskedNumber() + ", expiry=" + expiry + "}";
    }
}
