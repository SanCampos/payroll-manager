package Models;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

/**
 * Created by thedr on 6/4/2017.
 */
public class User {

    private final String username;
    private final String hash_pw;
    private final String salt;
    private int id;

    public User(String username, String hash_pw) {
        this.username = username;
        this.salt = BCrypt.gensalt(100, new SecureRandom());
        this.hash_pw = BCrypt.hashpw(hash_pw, this.salt);
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
