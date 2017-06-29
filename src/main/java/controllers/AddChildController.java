package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import main.java.customNodes.PersistentPromptTextField;

/**
 * Created by Santi on 6/29/2017.
 */
public class AddChildController {

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
        //Set prompt texts for all inputs
        firstNameInput.setPromptText("Juan");
        lastNameInput.setPromptText("dela Cruz");
        nickNameInput.setPromptText("\"John\"");
        birthPlaceInput.setPromptText("Negros Occ. Baco...");

        datePicker.setPromptText("Format - 06/12/2000");

        //Init gender choice buttons
        //OMG MY PATRIARCHY
        maleButton.setSelected(true);
    }

}
