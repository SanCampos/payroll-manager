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
import main.java.db.Database;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Santi on 6/29/2017.
 */
public class ChildFormController {
    
    @FXML
    private PersistentPromptTextField firstNameInput;
    @FXML
    private PersistentPromptTextField lastNameInput;
    @FXML
    private PersistentPromptTextField nickNameInput;
    @FXML
    private PersistentPromptTextField birthPlaceInput;
    
    @FXML
    private TextArea childDescInput;
    
    //TWO SCOOPS TWO GENDERS TWO TERMS
    @FXML
    private ToggleGroup genderToggleGroup;
    
    @FXML
    private DatePicker birthDateInput;
    
    @FXML
    private Label warnEmptyLabel;
    
    @FXML
    private ImageView childImage;
    @FXML
    private Label imageName;

    private FileInputStream slctdImgStrm;
    
    @FXML
    public void initialize() {
        //Init gender choice buttons and scene ref
        //OMG MY PATRIARCHY
        genderToggleGroup.getToggles().get(0).setSelected(true);
    }
    
    @FXML
    public void cancel(ActionEvent actionEvent) {
    }
    
    @FXML
    public void submit(ActionEvent actionEvent) throws ClassNotFoundException {
        //Clear warning label
        warnEmptyLabel.setStyle("-fx-text-fill: transparent");
    
        //Indicates that form is incomplete
        boolean incomplete = false;
    
        //Fetches textfield nodes from root
        List<Node> textFields = NodeUtils.getAllNodesOf(childImage.getParent(), new ArrayList<>(),
                "javafx.scene.control.TextInputControl");
    
        //Go mark each incomplete form
        for (Node n : textFields) {
            TextInputControl text = ((TextInputControl) n);
            String labelID = text.getId() == null ? "#birthDateWarning" : "#" + text.getId().replace("Input", "Warning");
    
            //Manipulate warning label if current node is NOT nickname textfield
            if (!labelID.contains("nick")) {
                Label warning = ((Label) childImage.getParent().lookup(labelID));
                if (text.getText().isEmpty()) {
                    warning.setStyle("-fx-text-fill: red");
                    incomplete = true;
                } else {
                    warning.setStyle("-fx-text-fill: transparent ");
                }
            }
        }
    
        //Notify user that form is incomplete
        if (incomplete) {
            warnEmptyLabel.setStyle("-fx-text-fill: red");
            return;
        }
    
        //Fetch user input
        String firstName = firstNameInput.getText();
        String lastName = lastNameInput.getText();
        String nickName = nickNameInput.getText();
        String place_of_birth = birthPlaceInput.getText();
        String childDesc = childDescInput.getText();
        String gender = ((String) genderToggleGroup.getSelectedToggle().getUserData());
    
        //Get child's age
        LocalDate birthDate = birthDateInput.getValue();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
    
    
        Database db = new Database();
    
        try {
            db.init();
            db.addNewChild(firstName, lastName, nickName, place_of_birth, age, childDesc, gender);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        firstNameInput.getScene().getWindow().hide();
    }
    
    @FXML
    public void changeChildImg(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File chosen = chooser.showOpenDialog(firstNameInput.getScene().getWindow());
        
        if (chosen == null) return;
    
        try {
            slctdImgStrm = new FileInputStream(chosen);
            childImage.setImage(new Image(slctdImgStrm));
            imageName.setText(chosen.getName());
            
            
        } catch (IOException e) {
            DialogUtils.showError("File error", "There was an error selecting your chosen file, please try again");
            e.printStackTrace();
        }
    }
}


