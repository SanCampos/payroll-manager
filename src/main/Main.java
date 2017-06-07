package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by thedr on 6/6/2017.
 */
public class Main extends Application {

    public static Stage loginStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage =  primaryStage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/fxml/Login.fxml"));
        Scene scene = new Scene(root, 500,  500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
