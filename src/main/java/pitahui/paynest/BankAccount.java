package pitahui.paynest;

/**
 * Apraksts (LV): Bankas konta datu klase — saglabā konta informāciju.
 * Description (EN): Bank account model class — holds account information.
 */

public class BankAccount {
    private Integer bankasKontsId;
    private Integer lietotajaId;
    private double bilance;
    private Integer kartesId;

    public BankAccount(Integer lietotajaId, double bilance) {
        // funkcija BankAccount (konstruktors) pieņem Integer tipa vērtību lietotajaId un double tipa vērtību bilance un atgriež void tipa vērtību
        this.bankasKontsId = null;
        this.lietotajaId = lietotajaId;
        this.bilance = bilance;
        this.kartesId = null;
    }

    public BankAccount(Integer bankasKontsId, Integer lietotajaId, double bilance) {
        // funkcija BankAccount (konstruktors) pieņem Integer tipa vērtību bankasKontsId, Integer tipa vērtību lietotajaId un double tipa vērtību bilance un atgriež void tipa vērtību
        this.bankasKontsId = bankasKontsId;
        this.lietotajaId = lietotajaId;
        this.bilance = bilance;
        this.kartesId = null;
    }

    public BankAccount(Integer bankasKontsId, Integer lietotajaId, double bilance, Integer kartesId) {
        // funkcija BankAccount (konstruktors) pieņem Integer tipa vērtību bankasKontsId, Integer tipa vērtību lietotajaId, double tipa vērtību bilance un Integer tipa vērtību kartesId un atgriež void tipa vērtību
        this.bankasKontsId = bankasKontsId;
        this.lietotajaId = lietotajaId;
        this.bilance = bilance;
        this.kartesId = kartesId;
    }

    public Integer getBankasKontsId() {
        // funkcija getBankasKontsId pieņem void tipa vērtību un atgriež Integer tipa vērtību bankasKontsId
        return bankasKontsId;
    }

    public void setBankasKontsId(Integer bankasKontsId) {
        // funkcija setBankasKontsId pieņem Integer tipa vērtību bankasKontsId un atgriež void tipa vērtību
        this.bankasKontsId = bankasKontsId;
    }

    public Integer getLietotajaId() {
        // funkcija getLietotajaId pieņem void tipa vērtību un atgriež Integer tipa vērtību lietotajaId
        return lietotajaId;
    }

    public void setLietotajaId(Integer lietotajaId) {
        // funkcija setLietotajaId pieņem Integer tipa vērtību lietotajaId un atgriež void tipa vērtību
        this.lietotajaId = lietotajaId;
    }

    public double getBilance() {
        // funkcija getBilance pieņem void tipa vērtību un atgriež double tipa vērtību bilance
        return bilance;
    }

    public void setBilance(double bilance) {
        // funkcija setBilance pieņem double tipa vērtību bilance un atgriež void tipa vērtību
        this.bilance = bilance;
    }

    public Integer getKartesId() {
        // funkcija getKartesId pieņem void tipa vērtību un atgriež Integer tipa vērtību kartesId
        return kartesId;
    }

    public void setKartesId(Integer kartesId) {
        // funkcija setKartesId pieņem Integer tipa vērtību kartesId un atgriež void tipa vērtību
        this.kartesId = kartesId;
    }

    @Override
    public String toString() {
        // funkcija toString pieņem void tipa vērtību un atgriež String tipa vērtību (objekta teksta pārstāvējums)
        return "BankAccount{" +
            "bankasKontsId=" + bankasKontsId +
            ", lietotajaId=" + lietotajaId +
            ", bilance=" + bilance +
            ", kartesId=" + kartesId +
            '}';
    }
}
