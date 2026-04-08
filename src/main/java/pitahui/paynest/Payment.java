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
        // funkcija Payment (konstruktors) pieņem double tipa vērtību sum, String tipa vērtību date_Time un String tipa vērtību status un atgriež void tipa vērtību
        Sum = sum;
        Date_Time = date_Time;
        Status = status;
    }

    public double getSum() {
        // funkcija getSum pieņem void tipa vērtību un atgriež double tipa vērtību Sum
        return Sum;
    }

    public String getDate_Time() {
        // funkcija getDate_Time pieņem void tipa vērtību un atgriež String tipa vērtību Date_Time
        return Date_Time;
    }

    public String getStatus() {
        // funkcija getStatus pieņem void tipa vērtību un atgriež String tipa vērtību Status
        return Status;
    }

    public void setSum(double sum) {
        // funkcija setSum pieņem double tipa vērtību sum un atgriež void tipa vērtību
        Sum = sum;
    }

    public void setDate_Time(String date_Time) {
        // funkcija setDate_Time pieņem String tipa vērtību date_Time un atgriež void tipa vērtību
        Date_Time = date_Time;
    }

    public void setStatus(String status) {
        // funkcija setStatus pieņem String tipa vērtību status un atgriež void tipa vērtību
        Status = status;
    }

    




}
