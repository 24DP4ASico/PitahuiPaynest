package pitahui.paynest;

/**
 * Apraksts (LV): Kartes modelis — satur kartes datus un util funkcijas.
 * Description (EN): Card model — stores card data and utility methods.
 */

public class Card {
    private Integer id;
    private Integer userId;
    private String cardNumber;
    private String expiry;
    private String cardholderName;

    public Card(Integer id, Integer userId, String cardNumber, String expiry, String cardholderName) {
        // funkcija Card pieņem Integer tipa vērtību id, Integer tipa vērtību userId, String tipa vērtību cardNumber, String tipa vērtību expiry un String tipa vērtību cardholderName un atgriež void tipa vērtību
        this.id = id;
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cardholderName = cardholderName;
    }

    public Integer getId() {
        // funkcija getId pieņem void tipa vērtību un atgriež Integer tipa vērtību id
        return id;
    }

    public Integer getUserId() {
        // funkcija getUserId pieņem void tipa vērtību un atgriež Integer tipa vērtību userId
        return userId;
    }

    public String getCardNumber() {
        // funkcija getCardNumber pieņem void tipa vērtību un atgriež String tipa vērtību cardNumber
        return cardNumber;
    }

    public String getExpiry() {
        // funkcija getExpiry pieņem void tipa vērtību un atgriež String tipa vērtību expiry
        return expiry;
    }

    public String getCardholderName() {
        // funkcija getCardholderName pieņem void tipa vērtību un atgriež String tipa vērtību cardholderName
        return cardholderName;
    }

    public String getMaskedNumber() {
        // funkcija getMaskedNumber pieņem void tipa vērtību un atgriež String tipa vērtību (maskēta kartes numura fragments)
        if (cardNumber == null) return "";
        String s = cardNumber.replaceAll("\\s+", "");
        if (s.length() <= 4) return s;
        String last4 = s.substring(s.length() - 4);
        return "**** **** **** " + last4;
    }

    @Override
    public String toString() {
        // funkcija toString pieņem void tipa vērtību un atgriež String tipa vērtību (objekta teksta pārstāvējums)
        return "Card{id=" + id + ", userId=" + userId + ", card=" + getMaskedNumber() + ", expiry=" + expiry + "}";
    }
}
