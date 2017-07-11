package main.java.Settings;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.db.Database;
import main.java.globalInfo.GlobalInfo;
import main.java.utils.DialogUtils;
import main.java.utils.ImageUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import static main.java.utils.ShapeUtils.getAvatarCircle;
import main.java.db.DbSchema.*;

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

    //Reference to stage
    private SettingsStage stage;
   

    @FXML
    public void initialize() {
        initAvatar();
        initChangeDetectors();
        initButtons();
        initStageRef();
    }
    
    private void initStageRef() {
        Platform.runLater(() -> stage = ((SettingsStage) prof_img.getScene().getWindow()));
    }
    
    private void initAvatar() {
        Image get = new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath());
        prof_img.setClip(getAvatarCircle());
        updateAvatar(GlobalInfo.getCurrProfImg().getName(), get);
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
        changeMade.addListener((observable, oldValue, newValue) -> {
            apply_btn.setDisable(!observable.getValue());
        });
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
            strgRef = new File(GlobalInfo.getEmployeesImgDir() + "\\" + selected.getName());

            //For comparison of selected image to current profile picture, also for its preview
            Image slctdImg =  new Image(slctdImgStrm);
            
            //"Refill img stream for later use
            slctdImgStrm = new FileInputStream(selected);

            //Flags no image change if user selects his current profile picture
            if (GlobalInfo.getCurrProfImg().getName().equals(strgRef.getName()) && ImageUtils.equals(new Image("file:///"  + GlobalInfo.getCurrProfImg().getAbsolutePath()), slctdImg)) {
                updateAvatar(selected.getName(), slctdImg);
                pictureChanged.set(false);
                return;
            }
            
            boolean confirm = DialogUtils.getConfirm("Confirm picture change", "Are you sure you want to change your profile picture?");
           
            if (!confirm) {
                return;
            }

            //Show preview of new avatar
            updateAvatar(selected.getName(), slctdImg);
            pictureChanged.set(true);
        } catch (IOException e) {
            e.printStackTrace();

            //Inform user that selected picture failed to be read
            DialogUtils.displayError("Picture retrieval error", "There was an error retrieving your chosen file. Please verify that your file exists and try again.");
        }
    }

    private void updateAvatar(String fileName, Image slctdImg) {
        prof_img.setImage(slctdImg);
        img_name.setText(fileName);
    }
    
    @FXML
    public void applyChanges() {
        applyImgChange();
        stage.setChange(true);
    }
    
    private void applyImgChange() {
        try {
            if (!(strgRef.exists() && strgRef.isFile())) {
                strgRef.getParentFile().mkdirs();
                strgRef.createNewFile();
            }
            
            
            Files.copy(slctdImgStrm, Paths.get(strgRef.getPath()), StandardCopyOption.REPLACE_EXISTING);
    
            Database database = new Database();
            database.init();
            database.updateImageOf(GlobalInfo.getUserID(), strgRef.getAbsolutePath().replace("\\", "\\\\"), table_users.name);
            
            GlobalInfo.setCurrProfImg(strgRef);
        } catch (IOException e) {
            DialogUtils.displayError("Settings error", "There was an error moving your image file, please try again!");
            e.printStackTrace();
        } catch (SQLException e) {
            DialogUtils.displayError("Database error", "There was an error uploading your changes to the server, please try again!");
            e.printStackTrace();
        } finally {
            Image setImage = new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath());
            String name = GlobalInfo.getCurrProfImg().getName();
            updateAvatar(name, setImage);
            resetChanges();
            ok_btn.requestFocus();
        }
    }
    
    private void resetChanges() {
        for (SimpleBooleanProperty s : changeList)
            s.set(false);
    }
    
    @FXML
    public void confirmChange() {
        if (changeMade.getValue()) {
            boolean confirm = DialogUtils.getConfirm("Settings", "Your settings have changed, would you like to save them?");
            if (confirm) {
                applyChanges();
            }
        }
        closeWindow();
    }
    
    @FXML
    private void closeWindow() {
        stage.close();
    }
}
