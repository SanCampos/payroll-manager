package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.globalInfo.FilePaths;
import main.java.globalInfo.UserInfo;
import main.java.utils.ImageUtils;

import java.io.*;
import java.util.Optional;

import static main.java.globalInfo.FilePaths.currProfImgPath;
import static main.java.utils.ShapeUtils.getAvatarCircle;
import static main.java.utils.FileUtils.getFileName;

/**
 * Created by thedr on 6/14/2017.
 */
public class SettingsController {

    @FXML private ImageView prof_img;

    //Booleans for detecting for settings changes

        //for change in profile picture
        private boolean imageChanged = false;

    //Reference to strg dir of profile image
    private File strgRef;

    //Stream for slctd img data
    private InputStream slctdImgStrm;

    //Reference to current user profile
    private Image currImg;

    @FXML
    public void initialize() {
        //Init avatar preview with curr prof img
        currImg = new Image(currProfImgPath);
        initAvatar();
    }

    private void initAvatar() {
        prof_img.setImage(currImg);
        prof_img.setClip(getAvatarCircle());
    }

    @FXML
    public void getPicFile() {
        //Makes user choose a picture file and copies that file to local storage directory

        //Let user select a new image
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.jpg, *.gif, *.png)",
                                                                         "*.png", "*.jpg", "*.gif")); // possible refactor?
        try {
            //Reference to selected picture
            File selected = chooser.showOpenDialog(prof_img.getScene().getWindow());

            //Return if user closes dialog w/o selection
            if (selected == null)
                return;

            //For copying and reading selected image
            slctdImgStrm = new FileInputStream(selected);

            //Reference to planned storage location for selected picture
            strgRef = new File(FilePaths.employeesImgDir + "\\" + UserInfo.userID + "\\" + selected.getName());

            //For comparison of selected image to current profile picture, also for its preview
            Image slctdImg =  new Image(slctdImgStrm);

            //Flags no image change if user selects his current profile picture
            if (getFileName(currProfImgPath).equals(getFileName(strgRef.getAbsolutePath())) && ImageUtils.equals(currImg, slctdImg)) {
                prof_img.setImage(slctdImg);
                imageChanged = false;
                return;
            }

            //Let user confirm that he/she wants to change profile picture
            Alert confirmOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
            confirmOverwrite.setTitle("Confirm picture change");
            confirmOverwrite.setHeaderText(null);
            confirmOverwrite.setContentText("Are you sure you want to change your profile picture?");

            Optional<ButtonType> result = confirmOverwrite.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }

            //Show preview of new avatar
            prof_img.setImage(slctdImg);
            imageChanged = true;
        } catch (IOException e) {
            e.printStackTrace();

            //Inform user that selected picture failed to be read
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Error");
            errorDialog.setHeaderText(null);
            errorDialog.setContentText("There was an error retrieving your chosen file. Please verify that your file exists and try again.");
            errorDialog.showAndWait();
        }
    }
}
