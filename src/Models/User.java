package Models;

/**
 * Created by thedr on 6/4/2017.
 */
public class User {

    private String username;
    private String password;
    private int id;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
