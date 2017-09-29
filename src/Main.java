import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllers.LoginController;
import main.java.db.Database;

import java.sql.SQLException;
import java.util.zip.ZipEntry;

/**
 * Created by thedr on 6/6/2017.
 */
public class Main extends Application {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //Database db =  new Database();
        //db.init();
        //db.registerUser("kalipay", "negrensefoundation", "Kalipay Foundation");
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1030, 800);
        primaryStage.setScene(scene);
        LoginController controller = loader.getController();
        controller.setLoginStage(primaryStage);
        primaryStage.show();
    }
}
