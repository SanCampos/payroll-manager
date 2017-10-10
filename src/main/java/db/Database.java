package main.java.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import main.java.globalInfo.GlobalInfo;
import main.java.globalInfo.ServerInfo;
import main.java.models.Child;
import main.java.models.Employee;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.db.DbSchema.*;
import main.java.utils.SocketUtils;
import org.apache.commons.text.WordUtils;
import org.mindrot.jbcrypt.BCrypt;

import static java.lang.Math.incrementExact;
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
        String url = "jdbc:mysql://127.0.0.1:3306/child_tracker_test?verifyServerCertificate=false&useSSL=true";
        String user = "root";
        String pass = "root";
        
        con = DriverManager.getConnection(url, user, pass);
    }
    
    /**
     * Registers new user and and stores their username and (hashed) password into database
     * Hashed user password is stored by (roughly) halving the salt and placing the first
     * half in front of the hashed pw and the second half after the hashed pw
     * EXAMPLE: salt = SALT, hash_pw  =  HASH
     *          password in database = SAHASHLT
     * Salt retrieval for verification is also done through this salt and password mix
     * @param username username
     * @param password unhashed password
     * @return returns true if user is successfully registered
     * @throws SQLException in case of connection failure
     */
    public boolean registerUser(String username, String password, String name) throws SQLException {
        //Insert row for user info
        String user_row = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                table_users.name, table_users.cols.username, table_users.cols.hash_pw, "name");

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
        prepStmnt.setString(3, name);
        int user_cols_changed = prepStmnt.executeUpdate();
        return user_cols_changed != 0;
    }
    
    /**
     * Logs the user in, please refer to {@link #registerUser(String, String) registerUser()} method
     * for hash algorithm
     * @param username
     * @param password un hashed password
     * @return
     * @throws SQLException
     */
    public boolean loginUser(String username, String password) throws SQLException /*REDUNDANT?*/ {
        ResultSet user = getSingleRow(table_users.name, table_users.cols.username, username);

        if (!user.next())
            return false;
        
        String hashed_pw = user.getString(table_users.cols.hash_pw);

        //Assemble salt from stored hash
        String salt = hashed_pw.substring(0, FHALF_LENGTH) + hashed_pw.substring(hashed_pw.length() - (FHALF_LENGTH - 1), hashed_pw.length());

        //misc user info
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
            String filePath = getAvatarPathOf(GlobalInfo.getUserID(), table_users.name);
            if (filePath == null) {
                GlobalInfo.setCurrProfImg(new Image("file:///" + new File("src\\main\\resources\\imgs\\default_avatar.png").getAbsolutePath()));
                String name = user.getString(6);
                GlobalInfo.setUserName(name);

                return true;
            }

            Image receivedImage = SocketUtils.receiveImageFrom(ServerInfo.USER_IMAGE_LOGIN_PORT, GlobalInfo.getUserID());
            GlobalInfo.setCurrProfImg(receivedImage);
            String name = user.getString(6);
            GlobalInfo.setUserName(name);
            
            return true;
        }
        return false;
    }
    
    private PreparedStatement stmntWithAllChildProperties(String sql, String fName, String lName, String nickname, String birthPlace, LocalDate birthDate, String description, int gender, String referrer, int status, LocalDate admissionDate) throws SQLException {
        int birthPlaceID = getSingleDataIDOf(birthPlace, table_locations.name);
        int referrerID = getSingleDataIDOf(referrer, table_referrers.name);
        
        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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

    public int addNewChild(String fName, String lName, String nickname, String birthPlace, LocalDate age, String description, int gender, String referrer, int status, LocalDate admissionDate) throws SQLException {
        //Add birthplace and referrer record
        addSingleUniqueData(birthPlace, table_locations.name);
        addSingleUniqueData(referrer, table_referrers.name);
        String insertChild = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, STR_TO_DATE(?, '%%Y-%%m-%%d'), ?, ?, ?, ?, ?)",
                table_children.name, table_children.cols.fname, table_children.cols.lname, table_children.cols.nickname, table_children.cols.place_of_birth, table_children.cols.birth_date,
                table_children.cols.description, table_children.cols.gender, table_children.cols.referrer_id, table_children.cols.status, table_children.cols.admission_date);

        PreparedStatement statement = stmntWithAllChildProperties(insertChild, fName, lName, nickname, birthPlace, age, description, gender, referrer, status, admissionDate);
        statement.executeUpdate();

        //return id of child for further use
        return statement.getGeneratedKeys().getInt(1);
    }
    
    private int getSingleDataIDOf(String data, String tableName) throws SQLException {
        Object id = getUniqueRowData(tableName, tableName.substring(0, tableName.length()-1), data, "id").get(0);
        if (id instanceof Long) {
            return toIntExact((long) id);
        }
        return (int) id;
    }

    public String getAvatarPathOf(int id, String tableName) throws SQLException {
        int avatarID = (int) getUniqueRowData(tableName, table_users.cols.id, id, "avatar_id").get(0);
        List<Object> uniqueRowData = getUniqueRowData(tableName + "_avatars", "id", avatarID, "path");
        if (uniqueRowData.size() > 0) return (String) uniqueRowData.get(0);
        else return null;
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
    private List<Object> getUniqueRowData(String tableName, String comparisonColumn, Object comparisonData, String desiredColumnData) throws SQLException {
        ResultSet row = getSingleRow(tableName, comparisonColumn, comparisonData);
        List<Object> data = new ArrayList<>();
        while (row.next()) {
            data.add(row.getObject(desiredColumnData));
        }
        return data;
    }

    private ResultSet getSingleRow(String tableName, String comparisonColumn, Object comparisonData) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableName, comparisonColumn);

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
            String place_of_birth = (String) getUniqueRowData(table_locations.name, table_locations.cols.id, children.getInt(table_children.cols.place_of_birth), table_locations.cols.location).get(0);
            String description = children.getString(table_children.cols.description);
            String referrer = (String) getUniqueRowData(table_referrers.name, table_referrers.cols.id, children.getInt(table_children.cols.referrer_id), table_referrers.cols.referrer).get(0);
            String gender = WordUtils.capitalize((String) getUniqueRowData(table_genders.name, table_genders.cols.id, children.getInt(table_children.cols.gender), table_genders.cols.gender).get(0));
            String status = WordUtils.capitalize((String) getUniqueRowData(table_children_statuses.name, table_children_statuses.cols.id, children.getInt(table_children.cols.status), table_children_statuses.cols.status).get(0));

            //fetch image of child
            int id = (int) children.getInt(table_children.cols.id);
            Image avatar = SocketUtils.receiveImageFrom(ServerInfo.CHILD_IMAGE_LOGIN_PORT, id);
            if (avatar == null) avatar = new Image("src\\main\\resources\\default_avatar.png");

            //fetch parents
            List<Object> parentIDs = getUniqueRowData(table_parent_child_relationships.name, table_parent_child_relationships.cols.child_id, id, table_parent_child_relationships.cols.parent_id);
            List<Child.Parent> parents = new ArrayList<>();
            
            for (Object o : parentIDs) {
                int parentID = ((int) o);
                ResultSet parent = con.createStatement().executeQuery(String.format("SELECT * FROM %s WHERE %s = %s", table_parents.name, table_parents.cols.id, parentID));
                if (parent.next()) {
                    String fName = parent.getString(table_parents.cols.first_name);
                    String lName = parent.getString(table_parents.cols.last_name);
                    String location = (String) getUniqueRowData(table_locations.name, table_locations.cols.id, parent.getInt(table_parents.cols.location_id), table_locations.cols.location).get(0);
                    String phoneNo = (String) getUniqueRowData(table_phone_numbers.name, table_phone_numbers.cols.id, parent.getInt(table_parents.cols.phone_number_id), table_phone_numbers.cols.number).get(0);
                    parents.add(new Child.Parent(fName, lName, phoneNo, location, parentID));
                }
            }
            
            String birth_date = children.getDate(table_children.cols.birth_date).toString();
            String admission_date = children.getDate(table_children.cols.admission_date).toString();


            childrenList.add(new Child(fname, lname, nickname, place_of_birth, description, gender, birth_date, admission_date, status, referrer, id, avatar, parents));
        }
        return childrenList;
    }



    public boolean updateImagePathOf(int userID, String path, String tableName) throws SQLException {
        //Insert new  row for image path to avatar  table
        String insertAvatarRow = String.format("INSERT IGNORE INTO %s (%s) VALUES (?)", tableName + "_avatars", table_userAvatars.cols.path);
        PreparedStatement insertNewAvatar = con.prepareStatement(insertAvatarRow);
        insertNewAvatar.setString(1, path);
        insertNewAvatar.execute();
        
        //Check if old avatar is still used by other users
        
        //Retrieve oldAvatarId and fetch rows of users who still use it
        Object oldAvatarID = getUniqueRowData(tableName, table_users.cols.id, userID, "avatar_id").get(0);
        
        System.out.println("Old Avatar ID = " + oldAvatarID);
        String getUsersOfOldAvatar = String.format("SELECT * FROM %s WHERE %s = ?", tableName, "avatar_id");
        System.out.println(getUsersOfOldAvatar);
        PreparedStatement getUsersOfID = con.prepareStatement(getUsersOfOldAvatar);
        getUsersOfID.setObject(1, oldAvatarID);
        
        //Delete old avatar if no other users use it
        
        //Retrieve id from avatar table and set user column to this id
        Object avatarID = getUniqueRowData(tableName + "_avatars", table_userAvatars.cols.path, path, table_userAvatars.cols.id).get(0);
        String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?", tableName, "avatar_id", table_users.cols.id);
        PreparedStatement updateAvatarIDS =  con.prepareStatement(sql);
        updateAvatarIDS.setObject(1, avatarID);
        updateAvatarIDS.setInt(2, userID);
        boolean updatedIDs = updateAvatarIDS.executeUpdate() != 0;
        
        
        ResultSet users = getUsersOfID.executeQuery();
        boolean isEmpty = !users.next();
        
        System.out.println(isEmpty);
        if (isEmpty) {
            List<Object> uniqueRowData = getUniqueRowData(tableName + "_avatars", table_userAvatars.cols.id, oldAvatarID, table_userAvatars.cols.path);
            if (uniqueRowData.size() > 0) {
                String oldAvatarPath = (String) uniqueRowData.get(0);
                System.out.println(oldAvatarPath);
                System.out.println(new File(oldAvatarPath).delete());

                String deleteOldAvatarRow = String.format("DELETE FROM %s WHERE %s = ?", tableName + "_avatars", table_userAvatars.cols.path);
                PreparedStatement statement = con.prepareStatement(deleteOldAvatarRow);
                statement.setString(1, oldAvatarPath);
                statement.execute();
            }
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
    
    public boolean updateParent(String fName, String lName, String address, String phoneNumber, int id) throws SQLException {
        addSingleUniqueData(address, table_locations.name);
        addSingleUniqueData(phoneNumber, table_phone_numbers.name);
    
        int locationID = getSingleDataIDOf(address, table_locations.name);
        int phoneNumberID = getSingleDataIDOf(phoneNumber, table_phone_numbers.name);
        
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", table_parents.name, table_parents.cols.first_name, table_parents.cols.last_name, table_parents.cols.location_id, table_parents.cols.phone_number_id, table_parents.cols.id);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, fName);
        preparedStatement.setString(2, lName);
        preparedStatement.setInt(3, locationID);
        preparedStatement.setInt(4, phoneNumberID);
        preparedStatement.setInt(5, id);
        
        return preparedStatement.executeUpdate() != 0;
    }
    
    public boolean updateChild(String firstName, String lastName, String nickName, String place_of_birth, LocalDate birthDate, String childDesc, int gender, String referrer, int status, LocalDate admissionDate, int id) throws SQLException {
        addSingleUniqueData(place_of_birth, table_locations.name);
        addSingleUniqueData(referrer, table_referrers.name);
        
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = STR_TO_DATE(?, '%%Y-%%m-%%d'), %s = ?, %s = ?, %s = ?, %s = ?, %s = STR_TO_DATE(?, '%%Y-%%m-%%d') WHERE %s = ? ",
                                    table_children.name, table_children.cols.fname,table_children.cols.lname, table_children.cols.nickname, table_children.cols.place_of_birth, table_children.cols.birth_date, table_children.cols.description, table_children.cols.gender, table_children.cols.referrer_id, table_children.cols.status, table_children.cols.admission_date, table_children.cols.id);
        PreparedStatement FUUUCK = stmntWithAllChildProperties(sql, firstName, lastName, nickName, place_of_birth, birthDate, childDesc, gender, referrer, status, admissionDate);
        FUUUCK.setInt(11, id);
        return FUUUCK.executeUpdate() != 0;
    }

    public boolean deleteParent(int parentID) throws SQLException {
        boolean parentRelationshipDeleted;
        boolean parentDeleted;

        String deleteRelationshipSQL = String.format("DELETE FROM %s WHERE %s = ?", table_parent_child_relationships.name, table_parent_child_relationships.cols.parent_id);
        PreparedStatement preparedStatement = con.prepareStatement(deleteRelationshipSQL);
        preparedStatement.setInt(1, parentID);
        parentRelationshipDeleted = preparedStatement.executeUpdate() != 0;


        List<Object> locationIDs = getUniqueRowData(table_parents.name, table_parents.cols.id, parentID, table_parents.cols.location_id);
        List<Object> phoneNumberIDs = getUniqueRowData(table_parents.name, table_parents.cols.id, parentID, table_parents.cols.phone_number_id);

        String deleteParentSQL = String.format("DELETE FROM %s WHERE %s = ?", table_parents.name, table_parents.cols.id);
        preparedStatement = con.prepareStatement(deleteParentSQL);
        preparedStatement.setInt(1, parentID);
        parentDeleted = preparedStatement.executeUpdate() != 0;


        con.createStatement().execute(String.format("DELETE IGNORE FROM %s WHERE %s = %s", table_locations.name, table_locations.cols.id, locationIDs.get(0)));
        con.createStatement().execute(String.format("DELETE IGNORE FROM %s WHERE %s = %s", table_phone_numbers.name, table_phone_numbers.cols.id, phoneNumberIDs.get(0)));

        return parentDeleted && parentRelationshipDeleted;
    }

    public void deleteChild(int childID) throws SQLException {
        List<Object> parentIDs = getUniqueRowData(table_parent_child_relationships.name, table_parent_child_relationships.cols.child_id, childID, table_parent_child_relationships.cols.parent_id);
        List<Object> locationIDs = new ArrayList<>();
        locationIDs.addAll(getUniqueRowData(table_children.name, table_children.cols.id, childID, table_children.cols.place_of_birth));

        List<Object> referrerIDs = new ArrayList<>();
        referrerIDs.addAll(getUniqueRowData(table_children.name, table_children.cols.id, childID, table_children.cols.referrer_id));

        List<Object> avatarIDs = new ArrayList<>();
        avatarIDs.addAll(getUniqueRowData(table_children.name, table_children.cols.id, childID, "avatar_id"));

        List<Object> avatarPaths = new ArrayList<>();
        for (Object o : avatarIDs) {
            avatarPaths.addAll(getUniqueRowData("children_avatars", "id", o, "path"));
        }

        try (Socket socket = new Socket(ServerInfo.SERVER_IP, ServerInfo.CHILD_DELETE_IMAGE_PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

             if (avatarPaths.size() > 0) {
                 out.writeUTF(((String) avatarPaths.get(0)));
             }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        con.createStatement().execute(String.format("DELETE IGNORE FROM %s WHERE %s = %s", table_children.name, table_children.cols.id, childID));

        for (int i = 0; i < parentIDs.size(); i++) {
            deleteParent(((int) parentIDs.get(i)));
        }

        for (Object o : locationIDs) {
            con.createStatement().execute(String.format("DELETE IGNORE FROM %s WHERE %s = %s", table_locations.name, table_locations.cols.id, o));
        }

        if (referrerIDs.size() > 0) {
            con.createStatement().execute(String.format("DELETE IGNORE FROM %s WHERE %s = %s", table_referrers.name, table_referrers.cols.id, referrerIDs.get(0)));
        }

        if (avatarIDs.size() > 0) {
            con.createStatement().execute(String.format("DELETE IGNORE FROM %s WHERE %s = %s", "children_avatars", "id", avatarIDs.get(0)));
        }

    }
}
