package pitahui.paynest;

public class Notification {

    private String message;
    private String date;
    private Integer dates_until_expiration;
    

    public Notification(String message, String date, Integer dates_until_expiration) {
        this.message = message;
        this.date = date;
        this.dates_until_expiration = dates_until_expiration;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public Integer getDates_until_expiration() {
        return dates_until_expiration;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDates_until_expiration(Integer dates_until_expiration) {
        this.dates_until_expiration = dates_until_expiration;
    }
    

    @Override
    public String toString() {
        return "Notification: " + "Message = " + message + ", Date = " + date + ", Days until expiration = " + dates_until_expiration;
    }
}

