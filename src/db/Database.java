package db;

import Models.Employee;

import java.lang.annotation.Target;
import java.sql.*;
import static db.DbSchema.TEST_TABLE;
import static db.DbSchema.TEST_TABLE.cols;

/**
 * Created by thedr on 5/31/2017.
 */
public class Database {
    //ALL THIS SHIT SHOULD NOT BE STATIC
    private static Connection con;

        public static void init() {
            String url = "jdbc:mysql://localhost:3306/test?verifyServerCertificate=false&useSSL=true";
            String user = "root";
            String pass = "root";


            try {
                con = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static Statement getScrollableStatement() {
            //Gets a statement that will return a scrollable read-only result set
            //WARNING: This method is for learning purposes for SQL. REMOVE UPON MVP COMPLETION
            try {
                return con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static Statement getStatement() {
            //Gets a statement that will return a scrollable read-only result set
            //WARNING: This method is for learning purposes for SQL. REMOVE UPON MVP COMPLETION
            try {
                return con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static boolean insertEmployee(Employee e) {
            //FOR MVP ONLY
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES ('%s', '%s', %s, %s)",
                                        TEST_TABLE.name, cols.first_name, cols.last_name, cols.age, cols.salary,
                                        e.getfName(), e.getlName(), e.getAge(), e.getSalary());

            try {
                return getStatement().executeUpdate(sql) != 0;
            } catch (NullPointerException | SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        public static boolean removeEmployee(int id) {
            String sql = String.format("DELETE FROM %s WHERE %s = %s", TEST_TABLE.name, cols.id, id);

            try {
                return getStatement().executeUpdate(sql) != 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean updateEmployee(int id, String[] fields, String[] data) {
            if (fields.length != data.length)
                throw new IllegalArgumentException("Each field/data input must have a corresponding data/field input!");

            String initial = String.format("UPDATE %s SET %s = '%s'", TEST_TABLE.name, fields[0], data[0]);
            StringBuilder sb = new StringBuilder(initial);

            for (int i = 1; i < fields.length; i++) {
                 String appendString = String.format(", %s = '%s'", fields[i], data[i]);
                 sb.append(appendString);
            }
                String appendString = String.format(" WHERE %s = %s", cols.id, id);
                sb.append(appendString);

            try {
                return getStatement().executeUpdate(sb.toString()) != 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
}
