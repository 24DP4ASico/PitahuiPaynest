package pitahui.paynest;

/**
 * Apraksts (LV): Lietotāja modelis — satur lietotāja datus un util funkcijas.
 * Description (EN): User model — contains user data and utility functions.
 */

import lv.pitahui.paynest.db.UserDAO;
import java.sql.SQLException;

public class User {
    private Integer lietotajaId;
    private String name;
    private String surname;
    private String phonenum;
    private String IBAN;
    private String password;
    private String language = "LV"; // default language (LV - Latvian)

    public User(String Name, String Surname, String Phonenum, String IBAN) {
        // funkcija User (konstruktors) pieņem String tipa vērtību Name, String tipa vērtību Surname, String tipa vērtību Phonenum un String tipa vērtību IBAN un atgriež void tipa vērtību
        this.lietotajaId = null;
        this.name = Name;
        this.surname = Surname;
        this.phonenum = Phonenum;
        this.IBAN = IBAN;
    }

    public User(Integer lietotajaId, String name, String surname, String phonenum, String IBAN) {
        // funkcija User (konstruktors) pieņem Integer tipa vērtību lietotajaId, String tipa vērtību name, String tipa vērtību surname, String tipa vērtību phonenum un String tipa vērtību IBAN un atgriež void tipa vērtību
        this.lietotajaId = lietotajaId;
        this.name = name;
        this.surname = surname;
        this.phonenum = phonenum;
        this.IBAN = IBAN;
    }

    public String getName() {
        // funkcija getName pieņem void tipa vērtību un atgriež String tipa vērtību name
        return name;
    }

    public void setName(String name) {
        // funkcija setName pieņem String tipa vērtību name un atgriež void tipa vērtību
        this.name = name;
    }

    public String getSurname() {
        // funkcija getSurname pieņem void tipa vērtību un atgriež String tipa vērtību surname
        return surname;
    }

    public void setSurname(String surname) {
        // funkcija setSurname pieņem String tipa vērtību surname un atgriež void tipa vērtību
        this.surname = surname;
    }

    public String getPhonenum() {
        // funkcija getPhonenum pieņem void tipa vērtību un atgriež String tipa vērtību phonenum
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        // funkcija setPhonenum pieņem String tipa vērtību phonenum un atgriež void tipa vērtību
        this.phonenum = phonenum;
    }

    public String getIBAN() {
        // funkcija getIBAN pieņem void tipa vērtību un atgriež String tipa vērtību IBAN
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        // funkcija setIBAN pieņem String tipa vērtību IBAN un atgriež void tipa vērtību
        this.IBAN = IBAN;
    }

    public Integer getId() {
        // funkcija getId pieņem void tipa vērtību un atgriež Integer tipa vērtību lietotajaId
        return lietotajaId;
    }

    public void setId(Integer lietotajaId) {
        // funkcija setId pieņem Integer tipa vērtību lietotajaId un atgriež void tipa vērtību
        this.lietotajaId = lietotajaId;
    }

    public String getPassword() {
        // funkcija getPassword pieņem void tipa vērtību un atgriež String tipa vērtību password
        return password;
    }

    public void setPassword(String password) {
        // funkcija setPassword pieņem String tipa vērtību password un atgriež void tipa vērtību
        this.password = password;
    }

    public String getLanguage() {
        // funkcija getLanguage pieņem void tipa vērtību un atgriež String tipa vērtību language
        return language;
    }

    public void setLanguage(String language) {
        // funkcija setLanguage pieņem String tipa vērtību language un atgriež void tipa vērtību
        this.language = language;
    }

    public void changeName(String newName) {
        // funkcija changeName pieņem String tipa vērtību newName un atgriež void tipa vērtību
        this.name = newName;
    }

    public void changeSurname(String newSurname) {
        // funkcija changeSurname pieņem String tipa vērtību newSurname un atgriež void tipa vērtību
        this.surname = newSurname;
    }

    public void changePhonenum(String newPhonenum) {
        // funkcija changePhonenum pieņem String tipa vērtību newPhonenum un atgriež void tipa vērtību
        this.phonenum = newPhonenum;
    }

    public void changeIBAN(String newIBAN) {
        // funkcija changeIBAN pieņem String tipa vērtību newIBAN un atgriež void tipa vērtību
        this.IBAN = newIBAN;
    }

    public void save() {
        // funkcija save pieņem void tipa vērtību un atgriež void tipa vērtību
        try {
            UserDAO.insert(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        // funkcija toString pieņem void tipa vērtību un atgriež String tipa vērtību (objekta teksta pārstāvējums)
        return "User:" + "Name = " + name + ", Surname = " + surname + ", Phonenum = " + phonenum + ", IBAN = " + IBAN;
    }
}
