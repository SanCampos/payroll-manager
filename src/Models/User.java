package Models;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

/**
 * Created by thedr on 6/4/2017.
 */
public class User { //NIGGA DO I EVEN NEED TO EXIST

    private final String username;
    private final String hash_pw;
    private final String salt;
    private int id;

    public User(String username, String hash_pw, String salt) {
        this.username = username;
        this.hash_pw = hash_pw;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public String getHash_pw() {
        return hash_pw;
    }

    public String getSalt /*--y lmao */ () {
        return salt;
    }
}
