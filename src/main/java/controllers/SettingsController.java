package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;

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

    public void getPicFile() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.jpg, *.png, *.gif)" , "*.jpg", "*.png", "*.gif"));
        File picFile = chooser.showOpenDialog(prof_img.getScene().getWindow());
    }
}
