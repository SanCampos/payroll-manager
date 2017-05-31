package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            } catch (SQLException exception) {
                System.out.println("CONNECTION FAILED: " + exception);
            }

        }


}
