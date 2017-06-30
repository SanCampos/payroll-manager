package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.db.Database;

import java.sql.SQLException;

/**
 * Created by thedr on 6/6/2017.
 */
public class Main extends Application {

    public static Stage loginStage;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //Database db =  new Database();
        //db.init();
        //db.registerUser("admin", "admin");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage =  primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 1000,  800);
        scene.getStylesheets().add(getClass().getResource("/css/persistent-prompt.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
