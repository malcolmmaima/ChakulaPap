package maima.chakulapap;

/**
 * Created by malcolm on 3/10/2017.
 */

public class User {

    String email, account, password;
    public String name;
    int phone;

    public User(String name, int phone, String email, String account, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.account = account;
        this.password = password;

    }

    public User(String email, String password) {
        this("", -1, email, "", password);
    }
}
