package ui.login;/**
 * Created by thedr on 6/6/2017.
 */

import db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class LoginController {

    @FXML private Text loginFailNotif;
    @FXML private TextField inputUser;
    @FXML private PasswordField inputPass;


    @FXML protected void login(ActionEvent event) {
        Database db = new Database();

        //Fetch user inputs
        String user = inputUser.getText();
        String pass = inputPass.getText();

        //Test for db connection and check if login is successful
        try {
            db.init();

            if (!db.loginUser(user, pass)) {
                loginFailNotif.setText("Your username/password is invalid");
                db.closeConnection();
                return;
            }

        } catch (SQLException e) {
            displaySQLError(e);
        }

    }

    private void displaySQLError(SQLException e) {
        loginFailNotif.setText("Error connecting to the server, please try again!");
        e.printStackTrace();
    }
}
