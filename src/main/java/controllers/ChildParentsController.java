package main.java.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import main.java.customNodes.PersistentPromptTextField;
import main.java.db.Database;
import main.java.models.Child;
import main.java.utils.DialogUtils;
import main.java.utils.NodeUtils;
import org.apache.commons.text.WordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thedr on 7/16/2017.
 */
public class ChildParentsController extends FormHelper {



    //Inputs for  first Parent information
    @FXML private PersistentPromptTextField firstParentFirstNameInput;
    @FXML private PersistentPromptTextField firstParentLastNameInput;
    @FXML private PersistentPromptTextField firstParentAddressInput;
    @FXML private PersistentPromptTextField firstParentPhoneNumberInput;
    
    //Inputs for  second Parent information
    @FXML private PersistentPromptTextField secondParentFirstNameInput;
    @FXML private PersistentPromptTextField secondParentLastNameInput;
    @FXML private PersistentPromptTextField secondParentAddressInput;
    @FXML private PersistentPromptTextField secondParentPhoneNumberInput;

    @FXML private CheckBox noFirstParentCheckBox;
    @FXML private CheckBox noSecondParentCheckBox;
    
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
                inputNodes = NodeUtils.getAllNodesOf(secondParentAddressInput.getParent(), new ArrayList<>(), "javafx.scene.control.TextInputControl");
            } catch (ClassNotFoundException e) {
                DialogUtils.displayExceptionError(e, "A severe error has occurred, please contact the developer for assistance");
                e.printStackTrace();
            }
        });
        noFirstParentCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> setDisableTo("first", newValue)));
        noSecondParentCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> setDisableTo("second", newValue)));
    
        bothParentsDisabled = noSecondParentCheckBox.selectedProperty().and(noFirstParentCheckBox.selectedProperty());
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

        firstParentAddressInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!secondParentAddressInput.isDisabled()) {
                secondParentAddressInput.setText(newValue);
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
    
    public void cancel(ActionEvent actionEvent) {
        if (!childFormController.getEdit())
            FormHelper.cancel(actionEvent, ((Stage) firstParentAddressInput.getScene().getWindow()));
        else
            ((Stage) firstParentPhoneNumberInput.getScene().getWindow()).close();
    }
    
    public void setPrevRoot(Parent root) {
        prevRoot = root;
    }
    
    public void submit() {
        if (isIncomplete()) return;

        int childID = childFormController.submit(false);

        try {
            Database db = new Database();
            db.init();

            addParent(childID, db, "first");
            addParent(childID, db, "second");

        } catch (SQLException e) {
            DialogUtils.displayError("Parent registration error!", "There was an error registering the parent data, please try again!");
            e.printStackTrace();
        }
        Stage thisStage = ((Stage) firstParentAddressInput.getScene().getWindow());
        thisStage.close();
    }

    private void addParent(int childID, Database db, String parent) throws SQLException {
            TextInputControl addressInput = getTextInputOf(parent, "ParentAddressInput");

        if (!addressInput.isDisabled()) {
            String fName = getTextInputTextOf(parent, "ParentFirstNameInput");
            String lName = getTextInputTextOf(parent, "ParentLastNameInput");
            String address = addressInput.getText();
            String phoneNumber = getTextInputTextOf(parent, "ParentPhoneNumberInput");

            db.addNewParent(fName, lName, address, phoneNumber, childID);
        }
    }

    private TextInputControl getTextInputOf(String parent, String input) {
        return (TextInputControl) firstParentAddressInput.getParent().lookup(String.format("#%s%s", parent, input));
    }

    private String getTextInputTextOf(String parent, String input) {
        return ((TextInputControl) firstParentAddressInput.getParent().lookup(String.format("#%s%s", parent, input))).getText();
    }

    public void goToPrevRoot() {
        firstParentAddressInput.getScene().setRoot(prevRoot);
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
    
    public void setParents(List<Child.Parent> parents) {
        Platform.runLater(() -> {
            noFirstParentCheckBox.setSelected(true);
            noSecondParentCheckBox.setSelected(true);
            for (int i = 0; i < parents.size(); i++) {
                setParentInfoOf(parents.get(i), getOrdinal(i+1));
            }
        });
    }
    
    private void setParentInfoOf(Child.Parent parent, String parentKind) {
        TextInputControl addressInput = getTextInputOf(parentKind, "ParentAddressInput");
        TextInputControl fName = getTextInputOf(parentKind, "ParentFirstNameInput");
        TextInputControl lName = getTextInputOf(parentKind, "ParentLastNameInput");
       
        TextInputControl phoneNumber = getTextInputOf(parentKind, "ParentPhoneNumberInput");
        CheckBox disableParent = (CheckBox) firstParentPhoneNumberInput.getParent().lookup(String.format("#no%sParentCheckBox", WordUtils.capitalize(parentKind)));
        disableParent.setSelected(false);
        
        fName.setText(parent.getfName());
        lName.setText(parent.getlName());
        addressInput.setText(parent.getAddress());
        phoneNumber.setText(parent.getPhoneNo());
    }
    
    
    //temporary
    private String getOrdinal(int i) {
        if (i == 1)
            return "first";
        else
            return "second";
    }
}
