package main.java.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.globalInfo.GlobalFiles;
import main.java.globalInfo.UserInfo;
import main.java.utils.ImageUtils;

import java.io.*;
import java.util.Optional;

import static main.java.globalInfo.GlobalFiles.currProfImg;
import static main.java.utils.ShapeUtils.getAvatarCircle;

/**
 * Created by thedr on 6/14/2017.
 */
public class SettingsController {

    @FXML private ImageView prof_img;
    @FXML private Label img_name;

    @FXML private Button ok_btn;
    @FXML private Button apply_btn;
    @FXML private Button cancel_btn;

    private SimpleBooleanProperty pictureChanged;
    private SimpleBooleanProperty testBooleanChanged;
    private BooleanBinding changeMade;

    //Reference to strg dir of profile image
    private File strgRef;

    //Stream for slctd img data
    private InputStream slctdImgStrm;

    //Reference to current user profile
    private Image currImg;

    @FXML
    public void initialize() {
        initAvatar();
        initChangeDetectors();
        initButtons();
    }

    private void initAvatar() {
        currImg = new Image("file:///" + currProfImg.getAbsolutePath());
        prof_img.setImage(currImg);
        prof_img.setClip(getAvatarCircle());
        img_name.setText(currProfImg.getName());
    }

    private void initChangeDetectors() {
        pictureChanged = new SimpleBooleanProperty(false);
        testBooleanChanged = new SimpleBooleanProperty(false);

        changeMade = new BooleanBinding() { //just add more bool properties  for each setting
            {
                super.bind(pictureChanged, testBooleanChanged);
            }

            @Override
            protected boolean computeValue() {
                return pictureChanged.get() || testBooleanChanged.get();
            }
        };
        changeMade.addListener(((observable, oldValue, newValue) ->
            apply_btn.setDisable(!observable.getValue())
        ));
    }

    private void initButtons() {
        Platform.runLater(() ->  ok_btn.requestFocus());
        apply_btn.setDisable(!changeMade.getValue());
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
            strgRef = new File(GlobalFiles.employeesImgDir + "\\" + UserInfo.userID + "\\" + selected.getName());

            //For comparison of selected image to current profile picture, also for its preview
            Image slctdImg =  new Image(slctdImgStrm);

            //Flags no image change if user selects his current profile picture
            if (currProfImg.getName().equals(strgRef.getName()) && ImageUtils.equals(currImg, slctdImg)) {
                updateAvatar(selected.getName(), slctdImg);
                pictureChanged.set(false);
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
            updateAvatar(selected.getName(), slctdImg);
            pictureChanged.set(true);
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

    private void updateAvatar(String fileName, Image slctdImg) {
        prof_img.setImage(slctdImg);
        img_name.setText(fileName);
    }
}
