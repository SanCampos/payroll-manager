import Models.Employee;
import Models.User;
import db.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;

/**
 * Created by thedr on 5/31/2017.
 */
public class Main {

    public static void main(String[] args) throws SQLException {
        //testing class
        Database db = new Database();
        db.init();
        Employee test = new Employee.Builder()
                                    .addfName("Johnlayn")
                                    .addlName("Mercado")
                                    .addAge(34)
                                    .addSalary(8900)
                                    .create();

        String salt = BCrypt.gensalt(15, new SecureRandom());
        String hash_pw = BCrypt.hashpw("admin", salt); //Nigga we need to speed this shit up
        User user = new User("admintest", hash_pw, salt);

        //System.out.println(db.insertEmployee(test));
        //System.out.println(db.removeEmployee(7));
        //System.out.println(db.updateEmployee(6, new String[]{"first_name", "salary"}, new String[]{"fsdffuck", "666.54"}));
        //System.out.println(db.getEmployeeInfo());
        db.registerUser(user);
    }
}
