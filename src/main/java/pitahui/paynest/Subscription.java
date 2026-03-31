package pitahui.paynest;

public class Subscription {
    private String subscriptionName;
    private String subscriptionType;
    private Integer subscriptionDuration;
    private Float subscriptionPrice;

    public Subscription(String subscriptionName, String subscriptionType, Integer subscriptionDuration, Float subscriptionPrice) {
        this.subscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.subscriptionDuration = subscriptionDuration;
        this.subscriptionPrice = subscriptionPrice;
    }
    public void subscriptionName(String subscriptionName){
        this.subscriptionName = subscriptionName;
    }
    public void subscriptionType(String subscriptionType){
        this.subscriptionType = subscriptionType;
    }
    public void subscriptionDuration(Integer subscriptionDuration){
        this.subscriptionDuration = subscriptionDuration;
    }

    public void subscriptionPrice(Float subscriptionPrice){
        this.subscriptionPrice = subscriptionPrice;
    }

    @Override
    public String toString() {
        return "Subscription:" + "Name = " + subscriptionName + ", Type = " + subscriptionType + ", Duration = " + subscriptionDuration + ", Price = " + subscriptionPrice;
    }
}
