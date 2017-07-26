package main.java.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.globalInfo.GlobalInfo;
import main.java.models.Child;
import main.java.models.Employee;

import java.io.File;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;

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
        String url = "jdbc:mysql://localhost:3306/tset?verifyServerCertificate=false&useSSL=true";
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
            File profImg= new File(getAvatarPathOf(GlobalInfo.getUserID(), table_users.name));
            GlobalInfo.setCurrProfImg(profImg);
            
            return true;
        }
        return false;
    }
    
    public int getChildIDOf(String fName, String lName, String nickname, String birthPlace, LocalDate birthDate, String description, int gender, String referrer, int status, LocalDate admissionDate) throws SQLException {
        String dateFormatString = "'%%Y-%%m-%%d'";
        String insertChild = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = STR_TO_DATE(?, " + dateFormatString + ") AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = STR_TO_DATE(?," + dateFormatString + ")",
                table_children.name, table_children.cols.fname, table_children.cols.lname, table_children.cols.nickname, table_children.cols.place_of_birth, table_children.cols.birth_date,
                table_children.cols.description, table_children.cols.gender, table_children.cols.referrer_id, table_children.cols.status, table_children.cols.admission_date);
        
        PreparedStatement statement = stmntWithAllChildProperties(insertChild, fName, lName, nickname, birthPlace, birthDate, description, gender, referrer, status, admissionDate);
        ResultSet child = statement.executeQuery();
        
        if (!child.next()) {
            return -89; //Arbitrary  negative number to indicate retrieval failure
        }
        
        return child.getInt(table_children.cols.id);
    }
    
    private PreparedStatement stmntWithAllChildProperties(String sql, String fName, String lName, String nickname, String birthPlace, LocalDate birthDate, String description, int gender, String referrer, int status, LocalDate admissionDate) throws SQLException {
        int birthPlaceID = getSingleDataIDOf(birthPlace, table_locations.name);
        int referrerID = getSingleDataIDOf(referrer, table_referrers.name);
        
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, fName);
        statement.setString(2, lName);
        statement.setString(3, nickname);
        statement.setInt(4, birthPlaceID);
        statement.setString(5, birthDate.toString());
        statement.setString(6, description);
        statement.setInt(7, gender);
        statement.setInt(8, referrerID);
        statement.setInt(9, status);
        statement.setString(10, admissionDate.toString());
        
        return statement;
    }

    public boolean addNewChild(String fName, String lName, String nickname, String birthPlace, LocalDate age, String description, int gender, String referrer, int status, LocalDate admissionDate) throws SQLException {
        //Add birthplace and referrer record
        addSingleUniqueData(birthPlace, table_locations.name);
        addSingleUniqueData(referrer, table_referrers.name);
        String insertChild = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, STR_TO_DATE(?, '%%Y-%%m-%%d'), ?, ?, ?, ?, ?)",
                table_children.name, table_children.cols.fname, table_children.cols.lname, table_children.cols.nickname, table_children.cols.place_of_birth, table_children.cols.birth_date,
                table_children.cols.description, table_children.cols.gender, table_children.cols.referrer_id, table_children.cols.status, table_children.cols.admission_date);

        PreparedStatement statement = stmntWithAllChildProperties(insertChild, fName, lName, nickname, birthPlace, age, description, gender, referrer, status, admissionDate);
        return statement.executeUpdate() != 0;
    }
    
    private int getSingleDataIDOf(String data, String tableName) throws SQLException {
        Object id = getSingleRowData(tableName, tableName.substring(0, tableName.length()-1), data, "id");
        if (id instanceof Long) {
            return toIntExact((long) id);
        }
        return (int) id;
    }

    public String getAvatarPathOf(int id, String tableName) throws SQLException {
        int avatarID = (int) getSingleRowData(tableName, table_users.cols.id, id, "avatar_id");
        return (String) getSingleRowData(tableName + "_avatars", table_userAvatars.cols.id, avatarID, table_userAvatars.cols.path);
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
    
    /**
     *
     * @param data  adds a new unique  piece of data to its specific SQL table, data is  unique  and id identified
     * @param tableName  name  of table
     * @throws SQLException
     */
    private void addSingleUniqueData(Object data, String tableName) throws SQLException {
        String sql = String.format("INSERT IGNORE INTO %s (%s) VALUES (?)", tableName, tableName.substring(0, tableName.length()-1));

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setObject(1, data);

        statement.execute();
    }

    public ObservableList<Employee> getEmployees() throws SQLException {
        ObservableList<Employee> results = FXCollections.observableArrayList();

        Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet data = statement.executeQuery("SELECT * FROM employees");

        while (data.next()) {
            String fName = data.getString(table_employees.cols.first_name);
            String lName = data.getString(table_employees.cols.last_name);
            double salary = data.getDouble(table_employees.cols.salary);
            int age = data.getInt(table_employees.cols.age);
            int id = data.getInt(table_employees.cols.id);
            results.add(new Employee(fName, lName, age, salary, id));
        }
        return results;
    }

    public ObservableList<Child> getChildren() throws SQLException {
        Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet children = statement.executeQuery("SELECT * FROM children");

        ObservableList<Child> childrenList = FXCollections.observableArrayList();

        while (children.next()) {
            //Fetch the ton of shit CAN WE PLEASE USE FUCKING JOOQ
            String fname = children.getString(table_children.cols.fname);
            String lname = children.getString(table_children.cols.lname);
            String nickname = children.getString(table_children.cols.nickname);
            String place_of_birth = children.getString(table_children.cols.place_of_birth);
            String description = children.getString(table_children.cols.description);
            String referrer = (String) getSingleRowData(table_referrers.name, table_referrers.cols.id, children.getInt(table_children.cols.referrer_id), table_referrers.cols.referrer);
            String gender = (String) getSingleRowData(table_genders.name, table_genders.cols.id, children.getInt(table_children.cols.gender), table_genders.cols.gender);
            String status = (String) getSingleRowData(table_children_statuses.name, table_children_statuses.cols.id, children.getInt(table_children.cols.status), table_children_statuses.cols.status);
            //fetch image of child
            int id = (int) children.getInt(table_children.cols.id);
            String avatarPath = getAvatarPathOf(id, table_children.name);
            File avatar = new File(avatarPath);

            String birth_date = children.getDate(table_children.cols.birth_date).toString();
            String admission_date = children.getDate(table_children.cols.admission_date).toString();

            childrenList.add(new Child(fname, lname, nickname, place_of_birth, description, gender, birth_date, admission_date, status, referrer, id, avatar));
        }
        return childrenList;
    }



    public boolean updateImageOf(int userID, String path, String tableName) throws SQLException {
        //Insert new  row for image path to avatar  table
        String insertAvatarRow = String.format("INSERT IGNORE INTO %s (%s) VALUES (?)", tableName + "_avatars", table_userAvatars.cols.path);
        PreparedStatement insertNewAvatar = con.prepareStatement(insertAvatarRow);
        insertNewAvatar.setString(1, path);
        insertNewAvatar.execute();
        
        //Check if old avatar is still used by other users
        
        //Retrieve oldAvatarId and fetch rows of users who still use it
        Object oldAvatarID = getSingleRowData(tableName, table_users.cols.id, userID, "avatar_id");
        
        System.out.println("Old Avatar ID = " + oldAvatarID);
        String getUsersOfOldAvatar = String.format("SELECT * FROM %s WHERE %s = ?", tableName, "avatar_id");
        System.out.println(getUsersOfOldAvatar);
        PreparedStatement getUsersOfID = con.prepareStatement(getUsersOfOldAvatar);
        getUsersOfID.setObject(1, oldAvatarID);
        
        //Delete old avatar if no other users use it
        
        //Retrieve id from avatar table and set user column to this id
        Object avatarID = getSingleRowData(tableName + "_avatars", table_userAvatars.cols.path, path, table_userAvatars.cols.id);
        String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?", tableName, "avatar_id", table_users.cols.id);
        PreparedStatement updateAvatarIDS =  con.prepareStatement(sql);
        updateAvatarIDS.setObject(1, avatarID);
        updateAvatarIDS.setInt(2, userID);
        boolean updatedIDs = updateAvatarIDS.executeUpdate() != 0;
        
        
        ResultSet users = getUsersOfID.executeQuery();
        boolean isEmpty = !users.next();
        
        System.out.println(isEmpty);
        if (isEmpty) {
            String oldAvatarPath = (String) getSingleRowData(tableName + "_avatars", table_userAvatars.cols.id, oldAvatarID, table_userAvatars.cols.path);
            System.out.println(oldAvatarPath);
            System.out.println(new File(oldAvatarPath).delete());
            
            String deleteOldAvatarRow = String.format("DELETE FROM %s WHERE %s = ?", tableName + "_avatars", table_userAvatars.cols.path);
            PreparedStatement statement = con.prepareStatement(deleteOldAvatarRow);
            statement.setString(1, oldAvatarPath);
            statement.execute();
        }
        return updatedIDs;
    }

    public void closeConnection() throws SQLException {
        con.close();
    }

    public boolean addNewParent(String fName, String lName, String address, String phoneNumber, int childID) throws SQLException {
        boolean parentAdded;
        boolean relationshipAdded;

        addSingleUniqueData(address, table_locations.name);
        addSingleUniqueData(phoneNumber, table_phone_numbers.name);

        int locationID = getSingleDataIDOf(address, table_locations.name);
        int phoneNumberID = getSingleDataIDOf(phoneNumber, table_phone_numbers.name);

        String insertParentSQL = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)", table_parents.name, table_parents.cols.first_name, table_parents.cols.last_name, table_parents.cols.location_id, table_parents.cols.phone_number_id);

        PreparedStatement preparedStatement = con.prepareStatement(insertParentSQL, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, fName);
        preparedStatement.setString(2, lName);
        preparedStatement.setInt(3, locationID);
        preparedStatement.setInt(4, phoneNumberID);

        parentAdded = preparedStatement.executeUpdate() != 0;

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

        if (!generatedKeys.next()) {
            throw new NullPointerException("Failed to retrieve generated parent ID");
        }

        Object parentID = generatedKeys.getObject(1);
        if (parentID instanceof Long) {
            parentID = toIntExact(((Long) parentID));
        }

        String insertRelationshipSQL = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", table_parent_child_relationships.name, table_parent_child_relationships.cols.parent_id, table_parent_child_relationships.cols.child_id);
        preparedStatement = con.prepareStatement(insertRelationshipSQL);
        preparedStatement.setInt(1, ((int) parentID));
        preparedStatement.setInt(2, childID);
        relationshipAdded = preparedStatement.executeUpdate() != 0;

        return parentAdded && relationshipAdded;
    }
}
