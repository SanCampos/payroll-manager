package main.java.Settings;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.db.Database;
import main.java.globalInfo.GlobalInfo;
import main.java.globalInfo.ServerInfo;
import main.java.utils.DialogUtils;
import main.java.utils.ImageUtils;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import main.java.db.DbSchema.*;
import main.java.utils.SocketUtils;
import org.apache.commons.io.FilenameUtils;

import static java.lang.Math.toIntExact;

/**
 * Created by thedr on 6/14/2017.
 */
public class SettingsController {

    //The form checks if changes are made and greys out the apply button
    // if no changes are made. This hashset acts as a counter for all
    // changes the user has made. Each unique setting adds/removes a unique
    // integer to this set to indicate a settings change. The integer is
    // the ordinal value of the setting.

    // There are currently '1' unique setting/s that can be changed, please
    // use the integer '2' for the next setting to be changed and increment
    // these two values upon addition of said setting

    //for change of user picture
    private static final int PICTURE_CHANGED = 0;



    @FXML private ImageView prof_img;
    @FXML private Label img_name;

    @FXML private Button ok_btn;
    @FXML private Button apply_btn;

    private Set<Integer> changes = new HashSet<>();

    //Reference to strg dir of profile image
    private File strgRef;

    //Stream for slctd img data
    private InputStream slctdImgStrm;

    //Reference to stage
    private SettingsStage stage;
    private File selected;


    @FXML
    public void initialize() {
        initAvatar();
        initButtons();
        initStageRef();
    }
    
    private void initStageRef() {
        Platform.runLater(() -> stage = ((SettingsStage) prof_img.getScene().getWindow()));
    }
    
    private void initAvatar() {
        updateAvatar(GlobalInfo.getCurrProfImg());
        prof_img.setClip(ImageUtils.getAvatarCircle(prof_img.getFitHeight()));
    }

    private void initButtons() {
        ok_btn.requestFocus();
        //make hash set empty value observable
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
            selected = chooser.showOpenDialog(prof_img.getScene().getWindow());

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
            if (GlobalInfo.getCurrProfImg().getHeight() > 0.0) {
                if (ImageUtils.equals(GlobalInfo.getCurrProfImg(), slctdImg)) {
                    updateAvatar(slctdImg);
                    changes.remove(PICTURE_CHANGED);
                    return;
                }
            }
            boolean confirm = DialogUtils.getConfirm("Confirm picture change", "Are you sure you want to change your profile picture?");
           
            if (!confirm) {
                return;
            }

            //Show preview of new avatar
            updateAvatar(slctdImg);
            changes.add(PICTURE_CHANGED);
        } catch (IOException e) {
            e.printStackTrace();

            //Inform user that selected picture failed to be read
            DialogUtils.displayError("Picture retrieval error", "There was an error retrieving your chosen file. Please verify that your file exists and try again.");
        }
    }

    private void updateAvatar(Image slctdImg) {
        prof_img.setImage(slctdImg);
    }
    
    @FXML
    public void applyChanges() {
        applyImgChange();
        changes.clear();
        stage.setChange(true);
    }
    
    private void applyImgChange() {
        SocketUtils.uploadImageto(ServerInfo.USER_IMAGE_REGISTER_PORT, selected, table_users.name, GlobalInfo.getUserID());

        GlobalInfo.setCurrProfImg(new Image("file:///" + selected.getAbsolutePath()));
        Image setImage = GlobalInfo.getCurrProfImg();
        updateAvatar(setImage);
        ok_btn.requestFocus();
    }
    
    @FXML
    public void confirmChange() {
        if (!changes.isEmpty()) {
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
