package main.java.controllers;

import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import main.java.customNodes.PersistentPromptTextField;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santi on 6/29/2017.
 */
public class childFormController {
    
    @FXML
    private PersistentPromptTextField firstNameInput;
    @FXML
    private PersistentPromptTextField lastNameInput;
    @FXML
    private PersistentPromptTextField nickNameInput;
    @FXML
    private PersistentPromptTextField birthPlaceInput;
    
    //TWO SCOOPS TWO GENDERS TWO TERMS
    @FXML
    private RadioButton maleButton;
    @FXML
    private RadioButton femaleButton;
    
    @FXML
    private DatePicker birthDateInput;
    
    @FXML
    private ImageView childImage;
    @FXML
    private Label imageName;
    
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
    public void submit(ActionEvent actionEvent) throws ClassNotFoundException {
        boolean incomplete = false;
        List<Node> textFields = NodeUtils.getAllNodesOf(childImage.getParent(), new ArrayList<>(),
                "javafx.scene.control.TextField", "javafx.scene.control.TextArea");
    
        for (Node n : textFields) {
            if (n.getId() == null) {
                Label warning = ((Label) childImage.getParent().lookup("#birthDateWarning"));
                setWarningsForEmpty((TextInputControl) n, warning);
                continue;
            }
            
            if (n.getId().contains("nick")) {
                continue;
            }
        
            if (n instanceof TextInputControl) {
                Label warning = ((Label) childImage.getParent().lookup("#" + n.getId().replace("Input", "Warning")));
                setWarningsForEmpty((TextInputControl) n, warning);
            }
        }
    }
    
    private void setWarningsForEmpty(TextInputControl n, Label warning) {
        boolean incomplete;
        if (n.getText().isEmpty()) {
            warning.setStyle("-fx-text-fill: red");
            incomplete = true;
        } else {
            warning.setStyle("-fx-text-fill: transparent ");
        }
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


