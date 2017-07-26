package main.java.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import main.java.customNodes.PersistentPromptTextField;
import main.java.db.Database;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thedr on 7/16/2017.
 */
public class ChildParentsController extends FormHelper {



    //Inputs for  mother information
    @FXML private PersistentPromptTextField motherFirstNameInput;
    @FXML private PersistentPromptTextField motherLastNameInput;
    @FXML private PersistentPromptTextField motherAddressInput;
    @FXML private PersistentPromptTextField motherPhoneNumberInput;
    
    //Inputs for  father information
    @FXML private PersistentPromptTextField fatherFirstNameInput;
    @FXML private PersistentPromptTextField fatherLastNameInput;
    @FXML private PersistentPromptTextField fatherAddressInput;
    @FXML private PersistentPromptTextField fatherPhoneNumberInput;
    
    @FXML private CheckBox noFatherCheckBox;
    @FXML private CheckBox noMotherCheckBox;
    
    @FXML private Button submit;

    //Error box and messages
    @FXML private Rectangle errorBox;
    @FXML private Label noParentsError;
    @FXML private Label incompleteError;

    private BooleanBinding bothParentsDisabled;

    private ChildFormController childFormController;
    
    private List<Node> inputNodes;
    
    private Parent prevRoot;
    
    private Scene thisScene;
    //For form validation
    
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            try {
                inputNodes = NodeUtils.getAllNodesOf(fatherAddressInput.getParent(), new ArrayList<>(), "javafx.scene.control.TextInputControl");
            } catch (ClassNotFoundException e) {
                DialogUtils.displayExceptionError(e, "A severe error has occurred, please contact the developer for assistance");
                e.printStackTrace();
            }
        });
        noFatherCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> setDisableTo("father", newValue)));
        noMotherCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> setDisableTo("mother", newValue)));
    
        bothParentsDisabled = noFatherCheckBox.selectedProperty().and(noMotherCheckBox.selectedProperty());
        bothParentsDisabled.addListener(((observable, oldValue, newValue) -> {
            boolean bothDisabled = newValue;

            //block user from submitting form and inform him or her of the nuaghty thing he/she had done
            //do vice versa if the naughty thing has been undone
            submit.setDisable(bothDisabled);

            if (bothDisabled) {
                errorBox.getStyleClass().add("error");
                noParentsError.getStyleClass().add("error");
                incompleteError.getStyleClass().remove("error");
            } else {
                errorBox.getStyleClass().removeAll("error");
                noParentsError.getStyleClass().remove("error");
            }
        }));
    }
    
    
    private void setDisableTo(String parent, boolean disableValue) {
        for (Node n : inputNodes) {
            TextInputControl textInput = ((TextInputControl) n);
            if (textInput.getId().contains(parent)) {
                textInput.setDisable(disableValue);
            }
        }
    }
    
    private boolean formIsInvalid() {
        return false;
    }
    
    public void cancel(ActionEvent actionEvent) {
        FormHelper.cancel(actionEvent, ((Stage) motherAddressInput.getScene().getWindow()));
    }
    
    public void setPrevRoot(Parent root) {
        prevRoot = root;
    }
    
    public void submit() {
        if (isIncomplete()) return;

        int childID = childFormController.submit();

        try {
            Database db = new Database();
            db.init();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void goToPrevRoot() {
        motherAddressInput.getScene().setRoot(prevRoot);
    }

    public void setChildFormController(ChildFormController childFormController) {
        this.childFormController = childFormController;
    }

    private boolean isIncomplete() {
        errorBox.getStyleClass().remove("error");
        incompleteError.getStyleClass().remove("error");

        for (Node n : inputNodes) {
            TextInputControl text = ((TextInputControl) n);
            if (text.isDisabled()) continue;

            if (text.getText().length() == 0) {
                errorBox.getStyleClass().add("error");
                incompleteError.getStyleClass().add("error");
                return true;
            }
        }
        return false;
    }
}
