package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import main.java.customNodes.PersistentPromptTextField;
import main.java.utils.DialogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Santi on 6/29/2017.
 */
public class childFormController {

    @FXML private PersistentPromptTextField firstNameInput;
    @FXML private PersistentPromptTextField lastNameInput;
    @FXML private PersistentPromptTextField nickNameInput;
    @FXML private PersistentPromptTextField birthPlaceInput;

    //TWO SCOOPS TWO GENDERS TWO TERMS
    @FXML private RadioButton maleButton;
    @FXML private RadioButton femaleButton;

    @FXML private DatePicker birthDateInput;
    
    @FXML private ImageView childImage;
    @FXML private Label imageName;
    
    @FXML
    public void initialize() {
        //Init gender choice buttons and scene ref
        //OMG MY PATRIARCHY
        maleButton.setSelected(true);
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
    }

    @FXML
    public void submit(ActionEvent actionEvent) {
    }
    
    @FXML
    public void changeChildImg(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File chosen = chooser.showOpenDialog(maleButton.getScene().getWindow());
        
        if (chosen == null) return;
    
        try {
            InputStream slctdImgStrm = new FileInputStream(chosen);
            childImage.setImage(new Image(slctdImgStrm));
            imageName.setText(chosen.getName());
            
        } catch (IOException e) {
            DialogUtils.showError("File error", "There was an error selecting your chosen file, please try again");
            e.printStackTrace();
        }
    }
}
