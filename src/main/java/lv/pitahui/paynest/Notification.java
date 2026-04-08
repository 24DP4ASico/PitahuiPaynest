package lv.pitahui.paynest;

/**
 * Apraksts (LV): Paziņojuma klase — satur informāciju par paziņojumiem un to metodi.
 * Description (EN): Notification class — holds notification data and methods.
 */

public class Notification {
    private Integer notificationId;
    private Integer lietotajaId;
    private Integer abonementaId;
    private String teksts;
    private String izveidesDateums;
    private Integer dienasLidзTerminam;

    public Notification(Integer notificationId, Integer lietotajaId, Integer abonementaId,
                        String teksts, String izveidesDateums, Integer dienasLidзTerminam) {
        // funkcija Notification (konstruktors) pieņem Integer tipa vērtību notificationId, Integer tipa vērtību lietotajaId, Integer tipa vērtību abonementaId, String tipa vērtību teksts, String tipa vērtību izveidesDateums un Integer tipa vērtību dienasLidзTerminam un atgriež void tipa vērtību
        this.notificationId = notificationId;
        this.lietotajaId = lietotajaId;
        this.abonementaId = abonementaId;
        this.teksts = teksts;
        this.izveidesDateums = izveidesDateums;
        this.dienasLidзTerminam = dienasLidзTerminam;
    }

    public Notification(Integer lietotajaId, Integer abonementaId,
                        String teksts, String izveidesDateums, Integer dienasLidзTerminam) {
        // funkcija Notification (konstruktors) pieņem Integer tipa vērtību lietotajaId, Integer tipa vērtību abonementaId, String tipa vērtību teksts, String tipa vērtību izveidesDateums un Integer tipa vērtību dienasLidзTerminam un atgriež void tipa vērtību
        this(null, lietotajaId, abonementaId, teksts, izveidesDateums, dienasLidзTerminam);
    }

    public Integer getNotificationId() { return notificationId; }
    // funkcija getNotificationId pieņem void tipa vērtību un atgriež Integer tipa vērtību notificationId
    public Integer getLietotajaId() { return lietotajaId; }
    // funkcija getLietotajaId pieņem void tipa vērtību un atgriež Integer tipa vērtību lietotajaId
    public Integer getAbonementaId() { return abonementaId; }
    // funkcija getAbonementaId pieņem void tipa vērtību un atgriež Integer tipa vērtību abonementaId
    public String getTeksts() { return teksts; }
    // funkcija getTeksts pieņem void tipa vērtību un atgriež String tipa vērtību teksts
    public String getIzveidesDateums() { return izveidesDateums; }
    // funkcija getIzveidesDateums pieņem void tipa vērtību un atgriež String tipa vērtību izveidesDateums
    public Integer getDienasLidзTerminam() { return dienasLidзTerminam; }
    // funkcija getDienasLidзTerminam pieņem void tipa vērtību un atgriež Integer tipa vērtību dienasLidзTerminam

    // funkcija setNotificationId pieņem Integer tipa vērtību notificationId un atgriež void tipa vērtību
    public void setNotificationId(Integer notificationId) { this.notificationId = notificationId; }
    // funkcija setLietotajaId pieņem Integer tipa vērtību lietotajaId un atgriež void tipa vērtību
    public void setLietotajaId(Integer lietotajaId) { this.lietotajaId = lietotajaId; }
    // funkcija setAbonementaId pieņem Integer tipa vērtību abonementaId un atgriež void tipa vērtību
    public void setAbonementaId(Integer abonementaId) { this.abonementaId = abonementaId; }
    // funkcija setTeksts pieņem String tipa vērtību teksts un atgriež void tipa vērtību
    public void setTeksts(String teksts) { this.teksts = teksts; }
    // funkcija setIzveidesDateums pieņem String tipa vērtību izveidesDateums un atgriež void tipa vērtību
    public void setIzveidesDateums(String izveidesDateums) { this.izveidesDateums = izveidesDateums; }
    // funkcija setDienasLidзTerminam pieņem Integer tipa vērtību dienasLidзTerminam un atgriež void tipa vērtību
    public void setDienasLidзTerminam(Integer dienasLidзTerminam) { this.dienasLidзTerminam = dienasLidзTerminam; }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", lietotajaId=" + lietotajaId +
                ", abonementaId=" + abonementaId +
                ", teksts='" + teksts + '\'' +
                ", izveidesDateums='" + izveidesDateums + '\'' +
                ", dienasLidзTerminam=" + dienasLidзTerminam +
                '}';
    }
}
