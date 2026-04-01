package pitahui.paynest;

import lv.pitahui.paynest.db.SubscriptionDAO;
import java.sql.SQLException;

public class Subscription {
    private String subscriptionName;
    private String subscriptionType;
    private String subscriptionDuration;
    private double subscriptionPrice;
    private Integer lietotajaId;

    public Subscription(String subscriptionName, String subscriptionType, String subscriptionDuration, double subscriptionPrice) {
        this.subscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.subscriptionDuration = subscriptionDuration;
        this.subscriptionPrice = subscriptionPrice;
        this.lietotajaId = null;
    }

    public Subscription(String subscriptionName, String subscriptionType, String subscriptionDuration, double subscriptionPrice, Integer lietotajaId) {
        this.subscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.subscriptionDuration = subscriptionDuration;
        this.subscriptionPrice = subscriptionPrice;
        this.lietotajaId = lietotajaId;
    }
    public void subscriptionName(String subscriptionName){
        this.subscriptionName = subscriptionName;
    }
    public void subscriptionType(String subscriptionType){
        this.subscriptionType = subscriptionType;
    }
    public void subscriptionDuration(String subscriptionDuration){
        this.subscriptionDuration = subscriptionDuration;
    }

    public void subscriptionPrice(double subscriptionPrice){
        this.subscriptionPrice = subscriptionPrice;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public String getSubscriptionDuration() {
        return subscriptionDuration;
    }

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public Integer getLietotajaId() {
        return lietotajaId;
    }

    public void setLietotajaId(Integer lietotajaId) {
        this.lietotajaId = lietotajaId;
    }

    public void save() {
        try {
            SubscriptionDAO.insert(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Subscription:" + "Name = " + subscriptionName + ", Type = " + subscriptionType + ", Duration = " + subscriptionDuration + ", Price = " + subscriptionPrice;
    }
}
