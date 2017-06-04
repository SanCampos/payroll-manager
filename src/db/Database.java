package db;

import Models.Employee;

import java.sql.*;
import java.text.DecimalFormat;

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

        private static boolean execute(String sql) throws SQLException {
            //Executes an action manipulating the table and returns if change was successful
            return con.createStatement().executeUpdate(sql) != 0;
        }

        public static boolean insertEmployee(Employee e) throws SQLException {
            //FOR MVP ONLY
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES ('%s', '%s', %s, %s)",
                                        TEST_TABLE.name, cols.first_name, cols.last_name, cols.age, cols.salary,
                                        e.getfName(), e.getlName(), e.getAge(), e.getSalary());

            return execute(sql);
        }

        public static boolean removeEmployee(int id) throws SQLException {
            String sql = String.format("DELETE FROM %s WHERE %s = %s", TEST_TABLE.name, cols.id, id);
            return execute(sql);
        }

        public static boolean updateEmployee(int id, String[] fields, String[] data) throws SQLException {
            if (fields.length != data.length)
                throw new IllegalArgumentException("Each field/data input must have a corresponding data/field input!");

            String update = String.format("UPDATE %s SET %s = '%s'", TEST_TABLE.name, fields[0], data[0]);
            StringBuilder sql = new StringBuilder(update);

            for (int i = 1; i < fields.length; i++) {
                 String set = String.format(", %s = '%s'", fields[i], data[i]);
                 sql.append(set);
            }

            String where = String.format(" WHERE %s = %s", cols.id, id);
            sql.append(where);

            return execute(sql.toString());
        }

    public static String getEmployeeInfo() throws SQLException {
            String SQL = String.format("SELECT * FROM %s", TEST_TABLE.name);

            String header = String.format("%s  %s  %s  %s  %s\n", cols.id, cols.first_name, cols.last_name, cols.age, cols.salary);
            StringBuilder output = new StringBuilder(header);
            DecimalFormat format = new DecimalFormat("#,###.00");

            Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = statement.executeQuery(SQL);

            while (results.next()) {
                String row = String.format("%s   %s    %s    %s    %s\n",
                                            results.getInt(cols.id),
                                            results.getString(cols.first_name),
                                            results.getString(cols.last_name),
                                            results.getInt(cols.age),
                                            format.format(results.getDouble(cols.salary)));
                output.append(row);
            }
            return output.toString();
        }
}
