package main.java.controllers;
/**
 * Created by thedr on 6/6/2017.
 */

import main.java.Main;
import main.java.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private Text loginFailNotif;
    @FXML private TextField inputUser;
    @FXML private PasswordField inputPass;


    @FXML protected void login(ActionEvent event) {
        //Get main.java.db helper
        Database db = new Database();

        //Fetch user inputs
        String user = inputUser.getText();
        String pass = inputPass.getText();

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
            Main.loginStage.close();
            

        } catch (SQLException |IOException e) /*Please rework this once we're sure we don't need io exceptions*/ {
            displaySQLError(e);
        }

    }

    private void displaySQLError(Exception e) {
        loginFailNotif.setText("Error connecting to the server, please try again!");
        e.printStackTrace();
    }
}
