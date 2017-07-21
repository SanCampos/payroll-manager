package main.java.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import main.java.customNodes.PersistentPromptTextField;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;

import javax.xml.soap.Text;
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
    
    
    //For easier access-- im low on time  ok
    PersistentPromptTextField[] motherInputs = new PersistentPromptTextField[]{motherPhoneNumberInput, motherLastNameInput, motherFirstNameInput, motherAddressInput};
    PersistentPromptTextField[] fatherInputs = new PersistentPromptTextField[]{fatherAddressInput, fatherFirstNameInput, fatherLastNameInput, fatherPhoneNumberInput};
    
    
    private Parent prevRoot;
    
    private Scene thisScene;
    //For form validation
    
    private boolean formIsInvalid() {
    
    }
    
    public void cancel(ActionEvent actionEvent) {
        FormHelper.cancel(actionEvent, ((Stage) motherAddressInput.getScene().getWindow()));
    }
}
