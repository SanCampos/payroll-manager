package main.java.controllers;
/**
 * Created by thedr on 6/6/2017.
 */

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import main.java.Main;
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
import main.java.globalInfo.GlobalInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class LoginController {

    @FXML private Label loginFailNotif;
    @FXML private TextField inputUser;
    @FXML private PasswordField inputPass;


    @FXML
    public void initialize() {
        //Set listeners for login on pressing enter
        inputUser.setOnKeyPressed(this::loginIfNoEmptyFields);
        inputPass.setOnKeyPressed(this::loginIfNoEmptyFields);
        loginFailNotif.setStyle("-fx-text-fill: transparent");
    }

    private void loginIfNoEmptyFields(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER &&
                (((!inputPass.getText().isEmpty()) && (!inputUser.getText().isEmpty()))))
                login();
    }

    @FXML
    protected void login()  {
        //Get main.java.db helper
        Database db = new Database();

        //Fetch user inputs
        String user = inputUser.getText();
        String pass = inputPass.getText();

        loginFailNotif.setStyle("-fx-text-fill: transparent");

        //Test for main.java.db connection and check if login is successful
        try {
            db.init();

            //Notify login failure
            if (!db.loginUser(user, pass)) {
                inputPass.clear();
                loginFailNotif.setStyle("-fx-text-fill: red");
                db.closeConnection();
                return;
            }

            //Initiate list window
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/list.fxml"));
            Scene scene = new Scene(root, 1030, 800);
            Stage listStage = new Stage();
            listStage.setTitle("List");
            listStage.setScene(scene);
            listStage.show();
            inputPass.clear();
            Main.loginStage.close();
        } catch (SQLException e) {
            loginFailNotif.setStyle("-fx-text-fill: red");
            loginFailNotif.setText("Error connecting to the server, please try again!");
            e.printStackTrace();
        } catch (IOException e) {
            loginFailNotif.setStyle("-fx-text-fill: red");
            loginFailNotif.setText("An error has occurred, please try again!");
            e.printStackTrace();
        }
    }
}
