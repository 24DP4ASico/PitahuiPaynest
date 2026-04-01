package lv.pitahui.paynest;

public class Notification {
    private Integer notificationId;
    private Integer lietotajaId;
    private Integer abonementaId;
    private String teksts;
    private String izveidesDateums;
    private Integer dienasLidзTerminam;

    // Constructor with all fields
    public Notification(Integer notificationId, Integer lietotajaId, Integer abonementaId, 
                       String teksts, String izveidesDateums, Integer dienasLidзTerminam) {
        this.notificationId = notificationId;
        this.lietotajaId = lietotajaId;
        this.abonementaId = abonementaId;
        this.teksts = teksts;
        this.izveidesDateums = izveidesDateums;
        this.dienasLidзTerminam = dienasLidзTerminam;
    }

    // Constructor without ID (for inserts)
    public Notification(Integer lietotajaId, Integer abonementaId, 
                       String teksts, String izveidesDateums, Integer dienasLidзTerminam) {
        this.lietotajaId = lietotajaId;
        this.abonementaId = abonementaId;
        this.teksts = teksts;
        this.izveidesDateums = izveidesDateums;
        this.dienasLidзTerminam = dienasLidзTerminam;
    }

    // Getters
    public Integer getNotificationId() {
        return notificationId;
    }

    public Integer getLietotajaId() {
        return lietotajaId;
    }

    public Integer getAbonementaId() {
        return abonementaId;
    }

    public String getTeksts() {
        return teksts;
    }

    public String getIzveidesDateums() {
        return izveidesDateums;
    }

    public Integer getDienasLidзTerminam() {
        return dienasLidзTerminam;
    }

    // Setters
    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public void setLietotajaId(Integer lietotajaId) {
        this.lietotajaId = lietotajaId;
    }

    public void setAbonementaId(Integer abonementaId) {
        this.abonementaId = abonementaId;
    }

    public void setTeksts(String teksts) {
        this.teksts = teksts;
    }

    public void setIzveidesDateums(String izveidesDateums) {
        this.izveidesDateums = izveidesDateums;
    }

    public void setDienasLidзTerminam(Integer dienasLidзTerminam) {
        this.dienasLidзTerminam = dienasLidзTerminam;
    }

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
