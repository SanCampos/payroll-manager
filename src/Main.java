import Models.Employee;
import db.Database;

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
        Database.insertEmployee(test);
    }
}
