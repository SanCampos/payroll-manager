package db;

import Models.Employee;

import java.sql.*;
import java.text.DecimalFormat;

import Models.User;
import db.DbSchema.*;

/**
 * Created by thedr on 5/31/2017.
 */
public class Database {

    private Connection con;
    private int userID;

        public void init() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/test?verifyServerCertificate=false&useSSL=true";
            String user = "root";
            String pass = "root";

            con = DriverManager.getConnection(url, user, pass);
        }
        
        public boolean registerUser(User user) throws SQLException {
            String sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                                        table_users.name, table_users.cols.username, table_users.cols.hash_pw, table_users.cols.salt);
            PreparedStatement prepStmnt = con.prepareStatement(sql);
            prepStmnt.setString(1, user.getUsername());
            prepStmnt.setString(2, user.getHash_pw());
            prepStmnt.setString(3, user.getSalt());

            return prepStmnt.executeUpdate() != 0;
        }

        public boolean insertEmployee(Employee e) throws SQLException {
            //FOR MVP ONLY
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                                        DbSchema.table_employees.name, table_employees.cols.first_name, table_employees.cols.last_name, table_employees.cols.age, table_employees.cols.salary);
            PreparedStatement prepStmnt = con.prepareStatement(sql);

            prepStmnt.setString(1, e.getfName());
            prepStmnt.setString(2, e.getlName());
            prepStmnt.setInt(3, e.getAge());
            prepStmnt.setDouble(4, e.getSalary());

            return prepStmnt.executeUpdate() != 0;
        }

        public boolean removeEmployee(int id) throws SQLException {
            String sql = String.format("DELETE FROM %s WHERE %s = ?", DbSchema.table_employees.name, table_employees.cols.id);
            PreparedStatement prepStmnt = con.prepareStatement(sql);
            prepStmnt.setInt(1, id);
            return prepStmnt.executeUpdate() != 0 ;
        }

        public boolean updateEmployee(int id, String[] fields, String[] data) throws SQLException {
            //APPARENTLY THIS  IS BAD DESIGN. FOR DEV USE ONLY
            if (fields.length != data.length)
                throw new IllegalArgumentException("Each field/data input must have a corresponding data/field input!");

            String update = String.format("UPDATE %s SET %s = '%s'", table_employees.name, fields[0], data[0]);
            StringBuilder sql = new StringBuilder(update);

            for (int i = 1; i < fields.length; i++) {
                 String set = String.format(", %s = '%s'", fields[i], data[i]);
                 sql.append(set);
            }

            String where = String.format(" WHERE %s = %s", table_employees.cols.id, id);
            sql.append(where);

            return con.createStatement().executeUpdate(sql.toString()) != 0;
        }

        public String getEmployeeInfo() throws SQLException {
            String SQL = String.format("SELECT * FROM %s", DbSchema.table_employees.name);
            Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = statement.executeQuery(SQL);
            
            String tableHeader = String.format("%s  %s  %s  %s  %s\n", table_employees.cols.id, table_employees.cols.first_name, table_employees.cols.last_name, table_employees.cols.age, table_employees.cols.salary);
            StringBuilder output = new StringBuilder(tableHeader);
            DecimalFormat format = new DecimalFormat("#,###.00");

            while (results.next()) {
                String row = String.format("%s   %s    %s    %s    %s\n",
                                            results.getInt(table_employees.cols.id),
                                            results.getString(table_employees.cols.first_name),
                                            results.getString(table_employees.cols.last_name),
                                            results.getInt(table_employees.cols.age),
                                            format.format(results.getDouble(table_employees.cols.salary)));
                output.append(row);
            }
            return output.toString();
        }
}
