package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import main.java.customNodes.PersistentPromptTextField;

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

    @FXML private DatePicker datePicker;

    @FXML
    public void initialize() {
        //Init gender choice buttons
        //OMG MY PATRIARCHY
        maleButton.setSelected(true);
    }

    public void cancel(ActionEvent actionEvent) {
    }

    public void submit(ActionEvent actionEvent) {
    }
}
