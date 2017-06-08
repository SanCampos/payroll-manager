package main.java.controllers;
/**
 * Created by thedr on 6/6/2017.
 */

import javafx.scene.input.KeyCode;
import main.java.stage_launchers.Login;
import main.java.db.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private Text loginFailNotif;
    @FXML private TextField inputUser;
    @FXML private PasswordField inputPass;


    @FXML
    public void initialize() {
        inputUser.setOnKeyPressed(this::checkForEmptyFields);
        inputPass.setOnKeyPressed(this::checkForEmptyFields);
    }

    private void checkForEmptyFields(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER &&
                (!(inputPass.getText().isEmpty() && inputUser.getText().isEmpty())))
                login();
    }

    @FXML
    protected void login() {
        //Get main.java.db helper
        Database db = new Database();

        //Fetch user inputs
        String user = inputUser.getText();
        String pass = inputPass.getText();

        loginFailNotif.setText("");
        //Test for main.java.db connection and check if login is successful
        try {
            db.init();

            if (!db.loginUser(user, pass)) {
                loginFailNotif.setText("Your username/password is invalid");
                db.closeConnection();
                return;
            }

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/List.fxml"));
            Scene scene = new Scene(root, 1000, 800);
            Stage listStage = new Stage();
            listStage.setTitle("List");
            listStage.setScene(scene);
            listStage.show();
            Login.loginStage.close();

        } catch (SQLException |IOException e) /*Please rework this once we're sure we don't need io exceptions*/ {
            displaySQLError(e);
        }

    }

    private void displaySQLError(Exception e) {
        loginFailNotif.setText("Error connecting to the server, please try again!");
        e.printStackTrace();
    }
}
