package main.java.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.globalInfo.GlobalInfo;
import main.java.models.Employee;

import java.io.File;
import java.security.SecureRandom;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import main.java.db.DbSchema.*;
import org.mindrot.jbcrypt.BCrypt;

import static java.lang.Math.toIntExact;

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
        String lHalf = hash_pw.substring(FHALF_LENGTH, SALT_LENGTH);
        hash_pw.replace(FHALF_LENGTH, SALT_LENGTH, "");
        hash_pw.append(lHalf);

        //Set parameters
        PreparedStatement prepStmnt = con.prepareStatement(user_row);
        prepStmnt.setString(1, username);
        prepStmnt.setString(2, hash_pw.toString());
        int user_cols_changed = prepStmnt.executeUpdate();

        return user_cols_changed != 0;
    }

    public boolean loginUser(String username, String password) throws SQLException /*REDUNDANT?*/ {
        // Hashed user password is stored by (roughly) halving the salt and placing the first
        // half in front of the hashed pw and the second half after the hashed pw
        // EXAMPLE: salt = SALT, hash_pw  =  HASH
        //          password in database = SAHASHLT
        //Salt retrieval for verification is also done through this salt and password mix

        //Prep the prepared statement

        //Basically fetch the user (if there is one)
        ResultSet user = getSingleRow(table_users.name, table_users.cols.username, username);

        if (!user.next())
            return false;

        //Fetch stored hash
        String hashed_pw = user.getString(table_users.cols.hash_pw);

        //Assemble salt from stored hash
        String salt = hashed_pw.substring(0, FHALF_LENGTH) + hashed_pw.substring(hashed_pw.length() - (FHALF_LENGTH - 1), hashed_pw.length());

        //Fetch misc user info
        int user_prvlg = user.getInt(table_users.cols.prvlg_lvl);
        int userID = user.getInt(table_users.cols.id);

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
            File profImg= new File(getAvatarPathOf(GlobalInfo.getUserID()));
            GlobalInfo.setCurrProfImg(profImg);
            
            return true;
        }
        return false;
    }
    
    public int getIDof(String fName, String lName, String nickname, String birthPlace, int age, String description, String gender) throws SQLException {
        String insertChild = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                table_children.name, table_children.cols.fname, table_children.cols.lname, table_children.cols.nickname, table_children.cols.place_of_birth, table_children.cols.age,
                table_children.cols.description, table_children.cols.gender);
        
        PreparedStatement statement = stmntWithAllChildProperties(insertChild, fName, lName, nickname, birthPlace, age, description, gender);
        ResultSet child = statement.executeQuery();
        
        if (!child.next()) {
            return -89; //Arbitrary  negative number to indicate retrieval failure
        }
        
        return child.getInt(table_children.cols.id);
    }
    
    private PreparedStatement stmntWithAllChildProperties(String sql, String fName, String lName, String nickname, String birthPlace, int age, String description, String gender) throws SQLException {
        int birthPlaceID = getBirthPlaceID(birthPlace);
        int genderID = getGenderID(gender);
        
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, fName);
        statement.setString(2, lName);
        statement.setString(3, nickname);
        statement.setInt(4, birthPlaceID);
        statement.setInt(5, age);
        statement.setString(6, description);
        statement.setInt(7, genderID);
        
        return statement;
    }

    public boolean addNewChild(String fName, String lName, String nickname, String birthPlace, int age, String description, String gender) throws SQLException {
        //Add new birthPlace record if doesn't  exist
        addNewBirthPlace(birthPlace);
        String insertChild = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
                table_children.name, table_children.cols.fname, table_children.cols.lname, table_children.cols.nickname, table_children.cols.place_of_birth, table_children.cols.age,
                table_children.cols.description, table_children.cols.gender);

        PreparedStatement statement = stmntWithAllChildProperties(insertChild, fName, lName, nickname, birthPlace, age, description, gender);
        return statement.executeUpdate() != 0;
    }

    private int getGenderID(String gender) throws SQLException {
        return (int) getSingleRowData(table_genders.name, table_genders.cols.gender, gender, table_genders.cols.id);
    }

    private int getBirthPlaceID(String birthPlace) throws SQLException {
        return toIntExact((long) getSingleRowData(table_places_of_birth.name, table_places_of_birth.cols.birthPlace, birthPlace, table_places_of_birth.cols.id));
    }

    public String getAvatarPathOf(int id) throws SQLException {
        int avatarID = (int) getSingleRowData(table_users.name, table_users.cols.id, id, table_users.cols.avatar_id);
        return (String) getSingleRowData(table_avatars.name, table_avatars.cols.id, avatarID, table_avatars.cols.path);
    }

    /**
     * Fetches a SINGLE table row which matches one column criteria
     * Used for retrieving unique, id-identified pieces of data
     *
     * @param tableName         name of table
     * @param comparisonColumn  column to be used in WHERE clause
     * @param comparisonData    data to be compared to each row's criteria column
     * @param desiredColumnData name of column from which the data will be retrieved from
     * @return A result set representing that single row
     */
    private Object getSingleRowData(String tableName, String comparisonColumn, Object comparisonData, String desiredColumnData) throws SQLException {
        ResultSet row = getSingleRow(tableName, comparisonColumn, comparisonData);

        if (row.next()) {
            return row.getObject(desiredColumnData);
        }
        return null;
    }

    private ResultSet getSingleRow(String tableName, String comparisonColumn, Object comparisonData) throws SQLException {
        String sql = String.format("SELECT * FROM  %s WHERE %s = ?", tableName, comparisonColumn);

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setObject(1, comparisonData);

        return statement.executeQuery();
    }

    private void addNewBirthPlace(String birthPlace) throws SQLException {
        String addBirthPlace = String.format("INSERT INTO %s (%s) VALUES (?)", table_places_of_birth.name, table_places_of_birth.cols.birthPlace);

        PreparedStatement statement = con.prepareStatement(addBirthPlace);
        statement.setString(1, birthPlace);

        statement.execute();
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
            int age = data.getInt(table_employees.cols.age);
            int id = data.getInt(table_employees.cols.id);
            results.add(new Employee(fName, lName, age, salary, id));
        }
        return results;
    }

    public boolean updateImageOf(int userID, String path) throws SQLException {
        //Insert new  row for image path to avatar  table
        String insertAvatarRow = String.format("INSERT IGNORE INTO %s (%s) VALUES (?)", table_avatars.name, table_avatars.cols.path);
        PreparedStatement insertNewAvatar = con.prepareStatement(insertAvatarRow);
        insertNewAvatar.setString(1, path);
        insertNewAvatar.execute();
        
        //Check if old avatar is still used by other users
        
        //Retrieve oldAvatarId and fetch rows of users who still use it
        int oldAvatarID = (int) getSingleRowData(table_users.name, table_users.cols.id, userID, table_users.cols.avatar_id);
        System.out.println("Old Avatar ID = " + oldAvatarID);
        String getUsersOfOldAvatar = String.format("SELECT * FROM %s WHERE %s = ?", table_users.name, table_users.cols.avatar_id);
        System.out.println(getUsersOfOldAvatar);
        PreparedStatement getUsersOfID = con.prepareStatement(getUsersOfOldAvatar);
        getUsersOfID.setInt(1, oldAvatarID);
        
        //Delete old avatar if no other users use it
        
        //Retrieve id from avatar table and set user column to this id
        int avatarID = toIntExact((long)getSingleRowData(table_avatars.name, table_avatars.cols.path, path, table_avatars.cols.id));
        String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?", table_users.name, table_users.cols.avatar_id, table_users.cols.id);
        PreparedStatement updateAvatarIDS =  con.prepareStatement(sql);
        updateAvatarIDS.setInt(1, avatarID);
        updateAvatarIDS.setInt(2, userID);
        boolean updatedIDs = updateAvatarIDS.executeUpdate() != 0;
        
        
        ResultSet users = getUsersOfID.executeQuery();
        boolean isEmpty = !users.next();
        
        System.out.println(isEmpty);
        if (isEmpty) {
            String oldAvatarPath = (String) getSingleRowData(table_avatars.name, table_avatars.cols.id, oldAvatarID, table_avatars.cols.path);
            System.out.println(oldAvatarPath);
            System.out.println(new File(oldAvatarPath).delete());
        } else {
            do {
                System.out.println(users.getString(table_users.cols.username));
                System.out.println(users.getInt(table_users.cols.id));
            } while (users.next());
        }
        return updatedIDs;
    }

    public void closeConnection() throws SQLException {
        con.close();
    }
}
