package pitahui.paynest;

public class Main {
    public static void main(String[] args) {
        User user1 = new User ("John", "Doe", "1234567890", "TR123456789012345678901234");
        System.out.println(user1);
        Subscription subscription1 = new Subscription("Netflix", "Entertainment", 12, 15.99f);
        System.out.println(subscription1);
    }

}
