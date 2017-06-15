package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.IO.FilePaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static main.java.controllers.ControllerUtils.getAvatarCircle;
import static main.java.controllers.ListController.db;

/**
 * Created by thedr on 6/14/2017.
 */
public class SettingsController {

    @FXML private ImageView prof_img;


    @FXML
    public void initialize() {
        initAvatar();
    }

    private void initAvatar() {
        prof_img.setImage(new Image(ListController.path));
        prof_img.setClip(getAvatarCircle());
    }

    @FXML
    public void getPicFile() {
        //Makes user choose a picture file and copies that file to local storage directory

        //Init dialog for user to select a new image
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.jpg, *.gif, *.png)",
                                                                         "*.png", "*.jpg", "*.gif")); // possible refactor?
        try {
            //Selected picture reference
            File master = chooser.showOpenDialog(prof_img.getScene().getWindow());

            //Return if user closes dialog w/o selection
            if (master == null)
                return;

            //For retrieval of selected image data
            FileInputStream stream = new FileInputStream(master);

            //Reference to storage location for selected picture
            File created = new File(FilePaths.employeesImgDir + "\\" + master.getName());

            //Asks to overwrite if a picture w/ the same name exists in the same dir
            if (created.exists() && created.isFile()) {
                Alert confirmOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
                confirmOverwrite.setTitle("Confirm picture overwrite");
                confirmOverwrite.setHeaderText(null);
                confirmOverwrite.setContentText("A picture with the same file name has already been saved, would you like to overwrite it?");

                Optional<ButtonType> result = confirmOverwrite.showAndWait();
                if (result.get() != ButtonType.OK) {
                    return;
                }

                //Show preview of new avatar
                prof_img.setImage(new Image(stream));
            }
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
