package db;

import Models.Employee;

import java.sql.*;
import java.text.DecimalFormat;

import db.DbSchema.table_employees;
import  db.DbSchema.table_employees.cols;

/**
 * Created by thedr on 5/31/2017.
 */
public class Database {

    private Connection con;

        public void init() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/test?verifyServerCertificate=false&useSSL=true";
            String user = "root";
            String pass = "root";

            con = DriverManager.getConnection(url, user, pass);
        }

        public boolean insertEmployee(Employee e) throws SQLException {
            //FOR MVP ONLY
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                                        DbSchema.table_employees.name, cols.first_name, cols.last_name, cols.age, cols.salary);
            PreparedStatement prepStmnt = con.prepareStatement(sql);

            prepStmnt.setString(1, e.getfName());
            prepStmnt.setString(2, e.getlName());
            prepStmnt.setInt(3, e.getAge());
            prepStmnt.setDouble(4, e.getSalary());

            return prepStmnt.executeUpdate() != 0;
        }

        public boolean removeEmployee(int id) throws SQLException {
            String sql = String.format("DELETE FROM %s WHERE %s = ?", DbSchema.table_employees.name, cols.id);
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

            String where = String.format(" WHERE %s = %s", cols.id, id);
            sql.append(where);

            return con.createStatement().executeUpdate(sql.toString()) != 0;
        }

    public String getEmployeeInfo() throws SQLException {
            String SQL = String.format("SELECT * FROM %s", DbSchema.table_employees.name);

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
