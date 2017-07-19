package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.customNodes.PersistentPromptTextField;
import main.java.utils.NodeUtils;

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
    
    private Parent prevRoot;
    
    private Scene thisScene;
    //For form validation
    private List inputList;
    
    @FXML
    public void initialize() throws ClassNotFoundException {
        inputList = NodeUtils.getAllNodesOf(motherAddressInput.getParent(), new ArrayList<>(), "javafx.scene.control.TextInputControl");
    }
    
    public void setPrevRoot(Parent prevRoot)  {
        this.prevRoot = prevRoot;
    }
    
    public void goToPrevRoot(ActionEvent actionEvent) {
        motherAddressInput.getScene().setRoot(prevRoot);
    }
    
    public void submit(ActionEvent actionEvent) {
    }
    
    public void cancel(ActionEvent actionEvent) {
        FormHelper.cancel(actionEvent, ((Stage) motherAddressInput.getScene().getWindow()));
    }
}
