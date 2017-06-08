package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import main.java.stage_launchers.Login;

import java.sql.SQLException;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private ImageView profImg;
    @FXML private Button logout;

    @FXML
    public void initialize() {
        initAvatar();
    }

    @FXML
    public void logout() throws SQLException {
        Stage stage = ((Stage) logout.getScene().getWindow());
        stage.close();
        Login.loginStage.show();
    }

    private void initAvatar() {
        Circle circle = new Circle(32.5, 32.5, 32.5); //DONT FUCKIN CHANGE THIS
        circle.setFill(Paint.valueOf("RED"));
        profImg.setClip(circle);
    }

}
