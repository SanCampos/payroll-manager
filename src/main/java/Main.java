package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.db.Database;

import javax.xml.crypto.Data;
import java.sql.SQLException;

/**
 * Created by thedr on 6/6/2017.
 */
public class Main extends Application {

    public static Stage loginStage;

    public static void main(String[] args) throws SQLException {
        //Database db =  new Database();
        //db.init();
        //db.registerUser("admin", "admin");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage =  primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/addChild.fxml"));
        Scene scene = new Scene(root, 1000,  800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
