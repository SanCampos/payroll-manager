package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.customNodes.PersistentPromptTextField;
import main.java.db.Database;
import main.java.db.DbSchema.*;
import main.java.globalInfo.GlobalInfo;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

    private String pathRef;
    
    @FXML
    public void initialize() throws FileNotFoundException {
        //Init gender choice buttons and scene ref
        //OMG MY PATRIARCHY
        genderToggleGroup.getToggles().get(0).setSelected(true);
        
        //Init default image for child
        File defaultFile = new File("src\\main\\resources\\imgs\\default-avatar.png");
        updateChosenImage(defaultFile);
    }
    
    @FXML
    public void cancel(ActionEvent actionEvent) {
        firstNameInput.getScene().getWindow().hide();
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

        
        //Fire up db helper and insert new child record
        Database db = new Database();
        
        //Retrieve record's ID for later use
        int id;
    
        try {
            //Add record for child and retrieve its id
            db.init();
            db.addNewChild(firstName, lastName, nickName, place_of_birth, age, childDesc, gender);
           
            //Retrieve id for use in storing img
            id = db.getIDof(firstName, lastName, nickName, place_of_birth, age, childDesc, gender);
            if (id == -89) throw new SQLException();
            File strgReg = new File(pathRef.replace("id", String.valueOf(id)));
            
            //Store img file for child avatar
            if (!(strgReg.exists() && strgReg.isFile())) {
                strgReg.getParentFile().mkdirs();
                strgReg.createNewFile();
            }
            
            Files.copy(slctdImgStrm, Paths.get(strgReg.getPath()), StandardCopyOption.REPLACE_EXISTING);
            db.updateImageOf(id, strgReg.getPath(), table_children.name);
        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtils.displayError("Error saving child data", "There was an error in saving all child data. Please try again!");
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.displayError("Error saving image", "There was an error saving the image of the child. " +
                    "All other data besides the image has been saved. Please attempt to add the child image in its own page.");
        } finally {
            firstNameInput.getScene().getWindow().hide();
        }
    }
    
    @FXML
    public void changeChildImg(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File chosen = chooser.showOpenDialog(firstNameInput.getScene().getWindow());
        
        if (chosen == null) return;
    
        try {
            updateChosenImage(chosen);
        } catch (IOException e) {
            DialogUtils.displayError("File error", "There was an error selecting your chosen file, please try again");
            e.printStackTrace();
        }
    }
    
    private void updateChosenImage(File chosen) throws FileNotFoundException {
        slctdImgStrm = new FileInputStream(chosen);
        childImage.setImage(new Image(slctdImgStrm));
        imageName.setText(chosen.getName());
        pathRef = GlobalInfo.getChildrenImgDir() + "\\"+ chosen.getName();
        slctdImgStrm = new FileInputStream(chosen);
    }
}


