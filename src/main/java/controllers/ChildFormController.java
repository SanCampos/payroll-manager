package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santi on 6/29/2017.
 */
public class ChildFormController extends FormHelper {
    
    @FXML
    private ImageView childImage;
    @FXML
    private Label imageName;
    
    @FXML
    private PersistentPromptTextField firstNameInput;
    @FXML
    private PersistentPromptTextField lastNameInput;
    @FXML
    private PersistentPromptTextField nickNameInput;
    @FXML
    private PersistentPromptTextField birthPlaceInput;
    @FXML
    private PersistentPromptTextField referrerInput;
    
    @FXML
    private DatePicker birthDateInput;
    @FXML
    private DatePicker admissionDateInput;
    
    @FXML
    private Button submitBtn;
    
    @FXML
    private ComboBox childStatus;
    
    //TWO SCOOPS TWO GENDERS TWO TERMS
    @FXML
    private ToggleGroup genderToggleGroup;
    
    @FXML
    private TextArea childDescInput;
    @FXML
    private Label warnEmptyLabel;

    private FileInputStream slctdImgStrm;

    private String pathRef;
    
    private Parent nextParent;
    
    private ChildParentsController childParentsController;
    
    @FXML
    public void initialize() throws FileNotFoundException {
        //Init gender choice buttons and scene ref
        //OMG MY PATRIARCHY
        genderToggleGroup.getToggles().get(0).setSelected(true);
        childStatus.getSelectionModel().selectFirst();
        
        //Init default image for child
        File defaultFile = new File("src\\main\\resources\\imgs\\default-avatar.png");
        updateChosenImage(defaultFile);

        //Init submit/next btn
        initNextBtn();
        
        childStatus.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(observable);
            System.out.println(newValue);
                if (newValue.intValue() == 2) {
                    initSubmitBtn();
                } else if (newValue.intValue() != 2 && oldValue.intValue() == 2) {
                    initNextBtn();
                }
        });
    }
    
    private void initNextBtn() {
        submitBtn.setText("Next");
        submitBtn.getStyleClass().remove("submit");
        submitBtn.getStyleClass().add("default");
        submitBtn.setOnAction(event -> initParentForm());
    }
    
    private void initParentForm() {
        if (formIsIncomplete())
            return;
        
        try {
            if (nextParent ==  null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childParentsForm.fxml"));
                Parent root = loader.load();
                childParentsController = loader.getController();
                setNextParent(root);
            }
            submitBtn.getScene().setRoot(nextParent);
            childParentsController.setPrevRoot(submitBtn.getParent());
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.displayExceptionError(e, "Severe error!");
        }
    }
    
    private void initSubmitBtn() {
        submitBtn.setText("Submit");
        submitBtn.getStyleClass().remove("default");
        submitBtn.getStyleClass().add("submit");
        submitBtn.setOnAction(event -> submit());
    }
    
    @FXML
    public void cancel(ActionEvent actionEvent) {
        FormHelper.cancel(actionEvent, ((Stage) submitBtn.getScene().getWindow()));
    }
    
    
    public void submit() {
        if (formIsIncomplete())
            return;
        
        //Fetch first part of user input
        String firstName = firstNameInput.getText();
        String lastName = lastNameInput.getText();
        String nickName = nickNameInput.getText();
        String place_of_birth = birthPlaceInput.getText();
        String childDesc = childDescInput.getText();
        String referrer = referrerInput.getText();
        
        int gender = genderToggleGroup.getToggles().indexOf(genderToggleGroup.getSelectedToggle());
        int status = childStatus.getSelectionModel().getSelectedIndex();
        
        //Get child's birthdate  and admission_date date
        LocalDate birthDate = birthDateInput.getValue();
        LocalDate admissionDate = admissionDateInput.getValue();
        
        //Fire up db helper and insert new child record
        Database db = new Database();
        
        //Retrieve record's ID for later use
        int id;
        
    
        try {
            //Add record for child and retrieve its id
            db.init();
            db.addNewChild(firstName, lastName, nickName, place_of_birth, birthDate, childDesc, gender, referrer, status, admissionDate);
           
            //Retrieve id for use in storing img
            id = db.getChildIDOf(firstName, lastName, nickName, place_of_birth, birthDate, childDesc, gender, referrer, status, admissionDate);
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
            return;
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.displayError("Error saving image", "There was an error saving the image of the child. " +
                    "All other data besides the image has been saved. Please attempt to add the child image in its own page.");
        }
        firstNameInput.getScene().getWindow().hide();
    }
    
    private boolean formIsIncomplete() {
        //Clear warning labels
        warnEmptyLabel.setStyle("-fx-text-fill: transparent");
        
        //Indicates that form is incomplete
        boolean incomplete = false;
        
        //Fetches textfield nodes from root
        try {
            List<Node> textFields = NodeUtils.getAllNodesOf(childImage.getParent(), new ArrayList<>(),
                    "javafx.scene.control.TextInputControl");
    
            //Go mark each incomplete form
            for (Node n : textFields) {
                TextInputControl text = ((TextInputControl) n);
        
                String[] ids;
        
                if (text.getId() == null) {
                    ids = new String[] {"birthDateWarning", "admissionDateWarning"};
                } else {
                    ids = new String[] {text.getId().replace("Input", "Warning")};
                }
        
                //Manipulate warning label if current node is NOT nickname textfield
                if (!ids[0].contains("nick")) {
                    for (int i = 0; i < ids.length; i++) {
                        Label warning = ((Label) childImage.getParent().lookup("#" + ids[i]));
                        if (text.getText().isEmpty()) {
                            warning.setStyle("-fx-text-fill: red");
                            incomplete = true;
                        } else {
                            warning.setStyle("-fx-text-fill: transparent ");
                        }
                    }
                }
            }
    
            //Notify user that form is incomplete
            if (incomplete) warnEmptyLabel.setStyle("-fx-text-fill: red");
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            DialogUtils.displayExceptionError(e, "An error has occurred! Please contact the developer for assistance!");
        }
        return incomplete;
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
    
    public void setNextParent(Parent nextParent) {
        this.nextParent = nextParent;
    }
}


