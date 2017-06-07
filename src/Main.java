import models.Employee;
import db.Database;

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

        //System.out.println(db.insertEmployee(test));
        //System.out.println(db.removeEmployee(7));
        //System.out.println(db.updateEmployee(6, new String[]{"first_name", "salary"}, new String[]{"fsdffuck", "666.54"}));
        //System.out.println(db.getEmployeeInfo());

        try{
            db.loginUser("fuck", "admin");
        } catch(IllegalArgumentException exception) {
            exception.printStackTrace();
            System.out.println("YO YOUR ERROR CHECKING WORKSjkslfajd;fdaklsjf");
        }
    }
}
