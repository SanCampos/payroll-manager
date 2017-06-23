package main.java.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.globalInfo.GlobalInfo;
import main.java.models.Employee;

import java.io.File;
import java.security.SecureRandom;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import main.java.db.DbSchema.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by thedr on 5/31/2017.
 */
public class Database {

    //Length of salt and first half for encryption algorithm
    private static final int SALT_LENGTH = 29;
    private static final int FHALF_LENGTH = 15;
    
    private Connection con;

    public void init() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/test?verifyServerCertificate=false&useSSL=true";
            String user = "root";
            String pass = "root";

            con = DriverManager.getConnection(url, user, pass);
        }

    public String getAvatarOf(int id) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", table_avatars.name, table_avatars.cols.id);

        PreparedStatement statement = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);

        ResultSet row = statement.executeQuery();

        if (row.next()) {
            return row.getString(table_avatars.cols.path);
        }
        return null;
    }
        
        public boolean registerUser(String username, String password) throws SQLException {
            // Hashed user password is stored by (roughly) halving the salt and placing the first
            // half in front of the hashed pw and the second half after the hashed pw
            // EXAMPLE: salt = SALT, hash_pw  =  HASH
            //          password in database = SAHASHLT
            //Salt retrieval for verification is also done through this salt and password mix
            
            //Insert row for user info
            String user_row = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                                        table_users.name, table_users.cols.username, table_users.cols.hash_pw);
            //Encrypt password
            String salt = BCrypt.gensalt(FHALF_LENGTH, new SecureRandom());
            StringBuilder hash_pw = new StringBuilder(BCrypt.hashpw(password, salt));

            //Move second half of salt to end of string
            String lHalf =  hash_pw.substring(FHALF_LENGTH, SALT_LENGTH);
            hash_pw.replace(FHALF_LENGTH, SALT_LENGTH, "");
            hash_pw.append(lHalf);

            //Set parameters
            PreparedStatement prepStmnt = con.prepareStatement(user_row);
            prepStmnt.setString(1, username);
            prepStmnt.setString(2, hash_pw.toString());
            int user_cols_changed = prepStmnt.executeUpdate();
            
            //Retrieve user id (FUCK)
            String retrieve = String.format("SELECT * FROM %s WHERE %s = ?", table_users.name, table_users.cols.username);
            PreparedStatement stmnt =  con.prepareStatement(retrieve);
            stmnt.setString(1, username);
            ResultSet curr_user = stmnt.executeQuery();
            curr_user.next();
            int id = curr_user.getInt(table_users.cols.id);
    
            //Insert row for user avatar info
            String avatar_row = String.format("INSERT INTO %s (%s) VALUES (?)", table_avatars.name, table_avatars.cols.id);
            PreparedStatement avatar = con.prepareStatement(avatar_row);
            avatar.setInt(1, id);
            int avatar_cols_changed = avatar.executeUpdate();
            
            return user_cols_changed != 0 && avatar_cols_changed != 0;
        }

        public boolean loginUser(String username, String password) throws SQLException /*REDUNDANT?*/ {
            //Basically fetch the user (if there is one)
            String sql = String.format("SELECT * FROM %s WHERE %s = ?", table_users.name, table_users.cols.username);

            // Hashed user password is stored by (roughly) halving the salt and placing the first
            // half in front of the hashed pw and the second half after the hashed pw
            // EXAMPLE: salt = SALT, hash_pw  =  HASH
            //          password in database = SAHASHLT
            //Salt retrieval for verification is also done through this salt and password mix

            //Prep the prepared statement
            PreparedStatement prepStmnt = con.prepareStatement(sql);
            prepStmnt.setString(1, username);

            ResultSet user = prepStmnt.executeQuery();

            if (!user.next())
                return false;

            //Fetch stored hash
            String hashed_pw = user.getString(table_users.cols.hash_pw);

            //Assemble salt from stored hash
            String salt = hashed_pw.substring(0, FHALF_LENGTH) + hashed_pw.substring(hashed_pw.length()-(FHALF_LENGTH-1), hashed_pw.length());

            //Fetch misc user info
            int user_prvlg  = user.getInt(table_users.cols.prvlg_lvl);
            int userID  = user.getInt(table_users.cols.id);

            //Hash input pw for verification
            StringBuilder hashed_input = new StringBuilder(BCrypt.hashpw(password, salt));
            String lHalf = hashed_input.substring(FHALF_LENGTH, SALT_LENGTH);
            hashed_input.replace(FHALF_LENGTH, SALT_LENGTH, "");
            hashed_input.append(lHalf);

            if (hashed_input.toString().equals(hashed_pw)) {
                //Set globally needed user info
                GlobalInfo.setUserID(userID);
                GlobalInfo.setPrvlg_lvl(user_prvlg);
                
                //Attempt to retrieve user avatar as a File, assign default value if failure
                File profImg;
                try {
                    profImg = new File(getAvatarOf(GlobalInfo.getUserID()));
                } catch (NullPointerException e) {
                    profImg = new File("C:\\Users\\thedr\\IdeaProjects\\database\\src\\main\\resources\\imgs\\default-avatar.png");
                }
                GlobalInfo.setCurrProfImg(profImg);

                return true;
            }
            return false;
        }

        public boolean insertEmployee(Employee e) throws SQLException {
            //FOR MVP ONLY
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                                        DbSchema.table_employees.name, table_employees.cols.first_name, table_employees.cols.last_name, table_employees.cols.age, table_employees.cols.salary);

            //Set parameters
            PreparedStatement prepStmnt = con.prepareStatement(sql);
            prepStmnt.setString(1, e.getfName());
            prepStmnt.setString(2, e.getlName());
            prepStmnt.setInt(3, e.getAge());
            prepStmnt.setString(4, e.getSalary());

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
            //Prepare and exec query
            String SQL = String.format("SELECT * FROM %s", DbSchema.table_employees.name);
            Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = statement.executeQuery(SQL);

            //Prepare format stuff
            String tableHeader = String.format("%s  %s  %s  %s  %s\n", table_employees.cols.id, table_employees.cols.first_name, table_employees.cols.last_name, table_employees.cols.age, table_employees.cols.salary);
            StringBuilder output = new StringBuilder(tableHeader);
            DecimalFormat format = new DecimalFormat("#,###.00");

            while (results.next()) {
                //Iterate through each row (employee) and add each bit of info to table
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

        public ObservableList<Employee> getEmployees() throws SQLException {
            ObservableList<Employee> results = FXCollections.observableArrayList();

            Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet data = statement.executeQuery("SELECT * FROM employees");

            while (data.next()) {
                Map<String, String> row = new HashMap<>();
                String fName = data.getString(table_employees.cols.first_name);
                String lName = data.getString(table_employees.cols.last_name);
                double salary = data.getDouble(table_employees.cols.salary);
                int    age    = data.getInt(table_employees.cols.age);
                int    id     =  data.getInt(table_employees.cols.id);
                results.add(new Employee(fName, lName, age, salary, id));
            }
            return results;
        }

        public boolean updateImageOf(int id, String path) throws SQLException {
            String sql = String.format("UPDATE %s SET %s = '%s' WHERE %s = ?", table_avatars.name, table_avatars.cols.path, path, table_avatars.cols.id);

            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            return statement.executeUpdate() != 0;
        }

        public void closeConnection() throws SQLException {
            con.close();
        }
}
