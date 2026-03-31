

public class Subscription {

    private String SubscriptionName;
    private String subscriptionType;
    private Integer subscriptionDuration;
    private Float subscriptionPrice;

    public Subscription(String subscriptionName, String subscriptionType, Integer subscriptionDuration, Float subscriptionPrice) {
        this.SubscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.subscriptionDuration = subscriptionDuration;
        this.subscriptionPrice = subscriptionPrice;
    }

    
}
