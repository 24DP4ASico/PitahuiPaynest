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
        this.lietotajaId = null;
        this.name = Name;
        this.surname = Surname;
        this.phonenum = Phonenum;
        this.IBAN = IBAN;
    }

    public User(Integer lietotajaId, String name, String surname, String phonenum, String IBAN) {
        this.lietotajaId = lietotajaId;
        this.name = name;
        this.surname = surname;
        this.phonenum = phonenum;
        this.IBAN = IBAN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public Integer getId() {
        return lietotajaId;
    }

    public void setId(Integer lietotajaId) {
        this.lietotajaId = lietotajaId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void changeName(String newName) {
        this.name = newName;
    }

    public void changeSurname(String newSurname) {
        this.surname = newSurname;
    }

    public void changePhonenum(String newPhonenum) {
        this.phonenum = newPhonenum;
    }

    public void changeIBAN(String newIBAN) {
        this.IBAN = newIBAN;
    }

    public void save() {
        try {
            UserDAO.insert(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "User:" + "Name = " + name + ", Surname = " + surname + ", Phonenum = " + phonenum + ", IBAN = " + IBAN;
    }
}
