package db;

import Models.Employee;

import java.sql.*;

/**
 * Created by thedr on 5/31/2017.
 */
public class Database {

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
            try {
                return con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static void insertEmployee(Employee e) {
            String sql = String.format("INSERT INTO test ")
        }
}
