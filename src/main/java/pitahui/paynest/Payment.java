package pitahui.paynest;

/**
 * Apraksts (LV): Maksājuma modelis — satur informāciju par maksājumiem.
 * Description (EN): Payment model — holds payment information.
 */

public class Payment {

    private double Sum;
    private String Date_Time;
    private String Status;

    public Payment(double sum, String date_Time, String status) {
        Sum = sum;
        Date_Time = date_Time;
        Status = status;
    }

    public double getSum() {
        return Sum;
    }

    public String getDate_Time() {
        return Date_Time;
    }

    public String getStatus() {
        return Status;
    }

    public void setSum(double sum) {
        Sum = sum;
    }

    public void setDate_Time(String date_Time) {
        Date_Time = date_Time;
    }

    public void setStatus(String status) {
        Status = status;
    }

    




}
