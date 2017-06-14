package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import static main.java.controllers.ControllerUtils.getAvatarCircle;

/**
 * Created by thedr on 6/14/2017.
 */
public class SettingsController {

    @FXML ImageView prof_img;

    @FXML
    public void initialize() {
        initAvatar();
    }

    private void initAvatar() {
        prof_img.setClip(getAvatarCircle());
    }
}
