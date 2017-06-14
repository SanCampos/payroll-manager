package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.IO.FilePaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
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
        //Makes user choose a picture file and copies that file to local picture directory

        //Init fileChooser (what kind of name is that)
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.jpg, *.gif, *.png)",
                                                                         "*.png", "*.jpg", "*.gif")); // possible refactor?

        try {
            //Create file objects for chosen and created file and load stream
            File master = chooser.showOpenDialog(prof_img.getScene().getWindow());
            File created = new File(FilePaths.localImgPath + "\\" + master.getName());
            FileInputStream stream = new FileInputStream(master);

            //Creates img and respective dir if !exists
            if (!(created.exists() && created.isFile())) {
                created.getParentFile().mkdirs();
                created.createNewFile();
            }

            //Finally copies the bytes of chosen file to the created one, overwriting if necessary
            Files.copy(stream, Paths.get(created.getPath()), REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();

            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Error");
            errorDialog.setHeaderText(null);
            errorDialog.setContentText("There was an error retrieving your chosen file. Please verify that your file exists and try again.");
            errorDialog.showAndWait();
        }
    }
}
