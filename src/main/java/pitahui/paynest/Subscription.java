package pitahui.paynest;

/**
 * Apraksts (LV): Abonementa modelis — tur informāciju par lietotāja abonementiem.
 * Description (EN): Subscription model — stores user's subscription data.
 */

import lv.pitahui.paynest.db.SubscriptionDAO;
import java.sql.SQLException;

public class Subscription {
    private String subscriptionName;
    private String subscriptionType;
    private Integer subscriptionDuration;
    private double subscriptionPrice;
    private Integer lietotajaId;

    public Subscription(String subscriptionName, String subscriptionType, Integer subscriptionDuration, double subscriptionPrice) {
        // funkcija Subscription (konstruktors) pieņem String tipa vērtību subscriptionName, String tipa vērtību subscriptionType, Integer tipa vērtību subscriptionDuration un double tipa vērtību subscriptionPrice un atgriež void tipa vērtību
        this.subscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.subscriptionDuration = subscriptionDuration;
        this.subscriptionPrice = subscriptionPrice;
        this.lietotajaId = null;
    }

    public Subscription(String subscriptionName, String subscriptionType, Integer subscriptionDuration, double subscriptionPrice, Integer lietotajaId) {
        // funkcija Subscription (konstruktors) pieņem String tipa vērtību subscriptionName, String tipa vērtību subscriptionType, Integer tipa vērtību subscriptionDuration, double tipa vērtību subscriptionPrice un Integer tipa vērtību lietotajaId un atgriež void tipa vērtību
        this.subscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.subscriptionDuration = subscriptionDuration;
        this.subscriptionPrice = subscriptionPrice;
        this.lietotajaId = lietotajaId;
    }
    // funkcija subscriptionName pieņem String tipa vērtību subscriptionName un atgriež void tipa vērtību
    public void subscriptionName(String subscriptionName){
        this.subscriptionName = subscriptionName;
    }
    // funkcija subscriptionType pieņem String tipa vērtību subscriptionType un atgriež void tipa vērtību
    public void subscriptionType(String subscriptionType){
        this.subscriptionType = subscriptionType;
    }
    // funkcija subscriptionDuration pieņem Integer tipa vērtību subscriptionDuration un atgriež void tipa vērtību
    public void subscriptionDuration(Integer subscriptionDuration){
        this.subscriptionDuration = subscriptionDuration;
    }

    public void subscriptionPrice(double subscriptionPrice){
        // funkcija subscriptionPrice pieņem double tipa vērtību subscriptionPrice un atgriež void tipa vērtību
        this.subscriptionPrice = subscriptionPrice;
    }

    public String getSubscriptionName() {
        // funkcija getSubscriptionName pieņem void tipa vērtību un atgriež String tipa vērtību subscriptionName
        return subscriptionName;
    }

    public String getSubscriptionType() {
        // funkcija getSubscriptionType pieņem void tipa vērtību un atgriež String tipa vērtību subscriptionType
        return subscriptionType;
    }

    public Integer getSubscriptionDuration() {
        // funkcija getSubscriptionDuration pieņem void tipa vērtību un atgriež Integer tipa vērtību subscriptionDuration
        return subscriptionDuration;
    }

    public double getSubscriptionPrice() {
        // funkcija getSubscriptionPrice pieņem void tipa vērtību un atgriež double tipa vērtību subscriptionPrice
        return subscriptionPrice;
    }

    public Integer getLietotajaId() {
        // funkcija getLietotajaId pieņem void tipa vērtību un atgriež Integer tipa vērtību lietotajaId
        return lietotajaId;
    }

    public void setLietotajaId(Integer lietotajaId) {
        // funkcija setLietotajaId pieņem Integer tipa vērtību lietotajaId un atgriež void tipa vērtību
        this.lietotajaId = lietotajaId;
    }

    public void save() {
        // funkcija save pieņem void tipa vērtību un atgriež void tipa vērtību
        try {
            SubscriptionDAO.insert(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        // funkcija toString pieņem void tipa vērtību un atgriež String tipa vērtību (objekta teksta pārstāvējums)
        return "Subscription:" + "Name = " + subscriptionName + ", Type = " + subscriptionType + ", Duration = " + subscriptionDuration + ", Price = " + subscriptionPrice;
    }
}
