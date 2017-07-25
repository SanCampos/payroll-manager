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
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.customNodes.PersistentPromptTextField;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thedr on 7/16/2017.
 */
public class ChildParentsController extends FormHelper {

    //Error box for no parent submission
    @FXML private StackPane errorNodesParent;

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
    
    private BooleanBinding bothParentsDisabled;
    
    private List<Node> nodeList;
    private List<Node> errorNodeList;
    
    private Parent prevRoot;
    
    private Scene thisScene;
    //For form validation
    
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            try {
                nodeList = NodeUtils.getAllNodesOf(fatherAddressInput.getParent(), new ArrayList<>(), "javafx.scene.control.TextInputControl");
                errorNodeList = NodeUtils.getAllNodesOf(errorNodesParent, new ArrayList<>(), "javafx.scene.shape.Rectangle", "javafx.scene.control.Label");
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
            for (Node n : errorNodeList) {
                if (bothDisabled) {
                    n.getStyleClass().add("error");
                    n.getStyleClass().remove("disabled");
                } else {
                    n.getStyleClass().remove("error");
                    n.getStyleClass().add("disabled");
                }
            }
        }));
    }
    
    
    private void setDisableTo(String parent, boolean disableValue) {
        for (Node n : nodeList) {
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
    
    }
    
    public void goToPrevRoot() {
        motherAddressInput.getScene().setRoot(prevRoot);
    }
}
