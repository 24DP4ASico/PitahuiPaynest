package pitahui.paynest;

import lv.pitahui.paynest.db.UserDAO;
import java.sql.SQLException;

public class User{

    private String Name;
    private String Surname;
    private String Phonenum;
    private String IBAN;
    private String password;

    public User(String Name, String Surname, String Phonenum, String IBAN) {
        this.Name = Name;
        this.Surname = Surname;
        this.Phonenum = Phonenum;
        this.IBAN = IBAN;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String Surname) {
        this.Surname = Surname;
    }

    public String getPhonenum() {
        return Phonenum;
    }

    public void setPhonenum(String Phonenum) {
        this.Phonenum = Phonenum;
    }

    public String getIBAN() {
        return IBAN;
    }
    
    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void changeName(String newName) {
        this.Name = newName;
    }

    public void changeSurname(String newSurname) {
        this.Surname = newSurname;
    }

    public void changePhonenum(String newPhonenum) {
        this.Phonenum = newPhonenum;
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
        return "User:" + "Name = " + Name + ", Surname = " + Surname + ", Phonenum = " + Phonenum + ", IBAN = " + IBAN;
    }

}
