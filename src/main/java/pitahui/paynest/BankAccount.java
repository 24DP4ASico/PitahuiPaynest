package pitahui.paynest;

public class BankAccount {
    private Integer bankasKontsId;
    private Integer lietotajaId;
    private double bilance;
    private Integer kartesId;

    public BankAccount(Integer lietotajaId, double bilance) {
        this.bankasKontsId = null;
        this.lietotajaId = lietotajaId;
        this.bilance = bilance;
        this.kartesId = null;
    }

    public BankAccount(Integer bankasKontsId, Integer lietotajaId, double bilance) {
        this.bankasKontsId = bankasKontsId;
        this.lietotajaId = lietotajaId;
        this.bilance = bilance;
        this.kartesId = null;
    }

    public BankAccount(Integer bankasKontsId, Integer lietotajaId, double bilance, Integer kartesId) {
        this.bankasKontsId = bankasKontsId;
        this.lietotajaId = lietotajaId;
        this.bilance = bilance;
        this.kartesId = kartesId;
    }

    public Integer getBankasKontsId() {
        return bankasKontsId;
    }

    public void setBankasKontsId(Integer bankasKontsId) {
        this.bankasKontsId = bankasKontsId;
    }

    public Integer getLietotajaId() {
        return lietotajaId;
    }

    public void setLietotajaId(Integer lietotajaId) {
        this.lietotajaId = lietotajaId;
    }

    public double getBilance() {
        return bilance;
    }

    public void setBilance(double bilance) {
        this.bilance = bilance;
    }

    public Integer getKartesId() {
        return kartesId;
    }

    public void setKartesId(Integer kartesId) {
        this.kartesId = kartesId;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
            "bankasKontsId=" + bankasKontsId +
            ", lietotajaId=" + lietotajaId +
            ", bilance=" + bilance +
            ", kartesId=" + kartesId +
            '}';
    }
}
