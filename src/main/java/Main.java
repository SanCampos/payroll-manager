package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.db.Database;
import main.java.models.Child;
import main.java.utils.DialogUtils;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

/**
 * Created by thedr on 6/6/2017.
 */
public class Main extends Application {

    public static Stage loginStage;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
       /* Database db =  new Database();
        db.init();
        db.registerUser("root", "root");  */
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage =  primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 1000,  800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
