import Models.Employee;
import db.Database;

import javax.xml.crypto.Data;

/**
 * Created by thedr on 5/31/2017.
 */
public class Main {

    public static void main(String[] args) {
        Database.init();
        Employee test = new Employee.Builder()
                                    .addfName("Johnlayn")
                                    .addlName("Mercado")
                                    .addAge(34)
                                    .addSalary(8900)
                                    .create();
        //System.out.println(Database.insertEmployee(test));
        //System.out.println(Database.removeEmployee(7));
        System.out.println(Database.updateEmployee(6, new String[]{"first_name", "salary"}, new String[]{"fsdffuck", "666.54"}));
    }
}
