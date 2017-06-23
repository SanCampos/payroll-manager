package main.java.Settings;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import jdk.nashorn.internal.objects.Global;
import main.java.db.Database;
import main.java.globalInfo.GlobalInfo;
import main.java.utils.DialogUtils;
import main.java.utils.ImageUtils;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Optional;

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

    //Change states for each setting
    private SimpleBooleanProperty  pictureChanged = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty testBooleanChanged = new SimpleBooleanProperty(false);
    
    //For ease  of use  when affecting all changed
    private SimpleBooleanProperty[] changeList = new SimpleBooleanProperty[] {pictureChanged, testBooleanChanged};
    

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
        currImg = new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath());
        prof_img.setClip(getAvatarCircle());
        updateAvatar(GlobalInfo.getCurrProfImg().getName(), currImg);
    }

    private void initChangeDetectors() {
        changeMade = new BooleanBinding() { //just add more bool properties  for each setting
            {
                  super.bind(changeList);
            }

            @Override
            protected boolean computeValue() {
                for (SimpleBooleanProperty s : changeList)
                    if (s.getValue())
                        return true;
                
                return false;
            }
        };
        changeMade.addListener(((observable, oldValue, newValue) ->
            apply_btn.setDisable(!observable.getValue())
        ));
    }

    private void initButtons() {
        ok_btn.requestFocus();
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
            strgRef = new File(GlobalInfo.getEmployeesImgDir() + "\\" + GlobalInfo.getUserID() + "\\" + selected.getName());

            //For comparison of selected image to current profile picture, also for its preview
            Image slctdImg =  new Image(slctdImgStrm);
            
            //"Refill img stream for later use
            slctdImgStrm = new FileInputStream(selected);

            //Flags no image change if user selects his current profile picture
            if (GlobalInfo.getCurrProfImg().getName().equals(strgRef.getName()) && ImageUtils.equals(currImg, slctdImg)) {
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
            DialogUtils.showError("Picture retrieval error", "There was an error retrieving your chosen file. Please verify that your file exists and try again.");
        }
    }

    private void updateAvatar(String fileName, Image slctdImg) {
        prof_img.setImage(slctdImg);
        img_name.setText(fileName);
    }
    
    @FXML
    public void applyChanges() {
        try {
            if (!(strgRef.exists() && strgRef.isFile())) {
                strgRef.getParentFile().mkdirs();
                strgRef.createNewFile();
            }
            
            
            Files.copy(slctdImgStrm, Paths.get(strgRef.getPath()), StandardCopyOption.REPLACE_EXISTING);
    
            Database database = new Database();
            database.init();
            database.updateImageOf(GlobalInfo.getUserID(), strgRef.getAbsolutePath().replace("\\", "\\\\"));
            
            GlobalInfo.setCurrProfImg(strgRef);
        } catch (IOException e) {
            DialogUtils.showError("Settings error", "There was an error moving your image file, please try again!");
            e.printStackTrace();
        } catch (SQLException e) {
            DialogUtils.showError("Database error", "There was an error uploading your changes to the server, please try again!");
            e.printStackTrace();
        } finally {
            Image setImage = new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath());
            String name = GlobalInfo.getCurrProfImg().getName();
            updateAvatar(name, setImage);
            resetChanges();
        }
    }
    
    private void resetChanges() {
        for (SimpleBooleanProperty s : changeList)
            s.set(false);
    }
}
