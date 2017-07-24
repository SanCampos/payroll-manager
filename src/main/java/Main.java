package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/childForm.fxml"));
        Scene scene = new Scene(root, 575,  675);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
