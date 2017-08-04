package main.java.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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

    @FXML private AnchorPane anchorPane;

    //MULTIDIMENSIONAL ARRRAY LISTSS

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
    private List<Child.Parent> parents;

    private ListController listController;
    @FXML private Button backBtn;

    private Integer childID;
    private ChildDisplayController displayController;
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

            if (parents == null) {
                if (bothDisabled) {
                    errorBox.getStyleClass().add("error");
                    noParentsError.getStyleClass().add("error");
                    incompleteError.getStyleClass().remove("error");
                } else {
                    errorBox.getStyleClass().removeAll("error");
                    noParentsError.getStyleClass().remove("error");
                }
            }
        }));
        secondParentAddressInput.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!firstParentAddressInput.getText().isEmpty() && secondParentAddressInput.getText().isEmpty()) {
                secondParentAddressInput.setText(firstParentAddressInput.getText());
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
        FormHelper.cancel(actionEvent, ((Stage) firstParentAddressInput.getScene().getWindow()));
    }

    public void setPrevRoot(Parent root) {
        prevRoot = root;
    }

    public void submit() {
        if (isIncomplete()) return;

        childID = childID == null ? childFormController.submit(false) : childID;

        try {
            Database db = new Database();
            db.init();
            int i = 0;

            if (parents != null) {
                for (; i < parents.size(); i++) {
                    updateOrDeleteParent(parents.get(i).getId(), db, getOrdinal(i + 1));
                }
            }

            for (; i < 2; i++) {
                addParent(childID, db, getOrdinal(i+1));
            }
            if (displayController != null) {
                displayController.childParents.getChildren().clear();
                listController.initTable();
                listController.updateChildOf(displayController);
            }
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


    private void updateOrDeleteParent(int parentID, Database db, String parent) throws SQLException {
        TextInputControl addressInput = getTextInputOf(parent, "ParentAddressInput");

        if (!addressInput.isDisabled()) {
            String fName = getTextInputTextOf(parent, "ParentFirstNameInput");
            String lName = getTextInputTextOf(parent, "ParentLastNameInput");
            String address = addressInput.getText();
            String phoneNumber = getTextInputTextOf(parent, "ParentPhoneNumberInput");
            db.updateParent(fName, lName, address, phoneNumber, parentID);
        } else if (addressInput.getText().contains("-- TO BE DELETED --")) {
            db.deleteParent(parentID);
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

    public void setParents(List<Child.Parent> parents, Integer id) {
        Platform.runLater(() -> {
            this.parents = parents;
            noFirstParentCheckBox.setSelected(true);
            noSecondParentCheckBox.setSelected(true);
            for (int i = 0; i < parents.size(); i++) {
                setParentInfoOf(parents.get(i), getOrdinal(i+1));
                setDeleteCheckBoxes(getOrdinal(i+1));
            }

            AnchorPane.setLeftAnchor(submit, AnchorPane.getLeftAnchor(backBtn));
            AnchorPane.setTopAnchor(submit, AnchorPane.getTopAnchor(backBtn));
            AnchorPane.setBottomAnchor(submit, AnchorPane.getBottomAnchor(backBtn));
            AnchorPane.setRightAnchor(submit, AnchorPane.getRightAnchor(backBtn));

            childID = id;
            backBtn.setDisable(true);
            backBtn.setVisible(false);

            submit.setDisable(true);

            initFirstParentMatchers();
            initSecondParentMatchers();
        });
    }

    private void setDeleteCheckBoxes(String parent) {
        CheckBox noParentCheckBox = (CheckBox) firstParentAddressInput.getParent().lookup(String.format("#no%sParentCheckBox", WordUtils.capitalize(parent)));

        CheckBox deleteCheckBox = new CheckBox("Delete parent");
        deleteCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            boolean toBeDeleted = newValue;

            if (toBeDeleted) {
                //multideimensionl array lists will fix this abominatino
                TextInputControl addressInput = getTextInputOf(parent, "ParentAddressInput");
                TextInputControl fName = getTextInputOf(parent, "ParentFirstNameInput");
                TextInputControl lName = getTextInputOf(parent, "ParentLastNameInput");
                TextInputControl phoneNumber = getTextInputOf(parent, "ParentPhoneNumberInput");
                markForDeletion(new TextInputControl[] {addressInput, fName, lName, phoneNumber});
            } else {
                setParentInfoOf(parents.get(getInt(parent)-1), parent);
            }
        }));
        anchorPane.getChildren().add(deleteCheckBox);
        AnchorPane.setLeftAnchor(deleteCheckBox, 385.0);
        AnchorPane.setTopAnchor(deleteCheckBox, AnchorPane.getTopAnchor(noParentCheckBox));
    }

    private void markForDeletion(TextInputControl[] inputs) {
        for (TextInputControl input: inputs) {
            input.getStyleClass().add("mark_delete");
            input.setText("-- TO BE DELETED --");
            input.setDisable(true);
        }
    }

    private void setParentInfoOf(Child.Parent parent, String parentKind) {
        TextInputControl addressInput = getTextInputOf(parentKind, "ParentAddressInput");
        TextInputControl fName = getTextInputOf(parentKind, "ParentFirstNameInput");
        TextInputControl lName = getTextInputOf(parentKind, "ParentLastNameInput");
        TextInputControl phoneNumber = getTextInputOf(parentKind, "ParentPhoneNumberInput");
        CheckBox disableCheckBox = (CheckBox) firstParentPhoneNumberInput.getParent().lookup(String.format("#no%sParentCheckBox", WordUtils.capitalize(parentKind)));
        disableCheckBox.setSelected(false);

        fName.setText(parent.getfName());
        lName.setText(parent.getlName());
        addressInput.setText(parent.getAddress());
        phoneNumber.setText(parent.getPhoneNo());

        fName.getStyleClass().remove("mark_delete");
        lName.getStyleClass().remove("mark_delete");
        addressInput.getStyleClass().remove("mark_delete");
        phoneNumber.getStyleClass().remove("mark_delete");
    }


    //temporary
    private String getOrdinal(int i) {
        if (i == 1)
            return "first";
        else
            return "second";
    }

    private int getInt(String ordinal) {
        if (ordinal.equals("first")) {
            return 1;
        } else {
            return 2;
        }
    }

    public void initFirstParentMatchers() {
        SimpleBooleanProperty firstParentFirstNameMatch = new SimpleBooleanProperty(true);
        setListener(firstParentFirstNameInput, firstParentFirstNameMatch);

        SimpleBooleanProperty firstParentLastNameMatch = new SimpleBooleanProperty(true);
        setListener(firstParentLastNameInput, firstParentLastNameMatch);

        SimpleBooleanProperty firstParentAddressMatch = new SimpleBooleanProperty(true);
        setListener(firstParentAddressInput, firstParentAddressMatch);

        SimpleBooleanProperty firstParentPhoneNumberMatch = new SimpleBooleanProperty(true);
        setListener(firstParentPhoneNumberInput, firstParentPhoneNumberMatch);

        SimpleBooleanProperty[] matches = new SimpleBooleanProperty[] {firstParentAddressMatch, firstParentFirstNameMatch,firstParentLastNameMatch, firstParentPhoneNumberMatch};

        BooleanBinding firstMatches = new BooleanBinding() {
            {
                super.bind(matches);
            }
            @Override
            protected boolean computeValue() {
                for (SimpleBooleanProperty booleanProperty : matches) {
                    if (!booleanProperty.get()) {
                        return false;
                    }
                }
                return true;
            }
        };
        firstMatches.addListener(editListener());
    }

    private void setListener(TextInputControl textInputControl, SimpleBooleanProperty matcher) {
        textInputControl.textProperty().addListener(matchListener(textInputControl.getText(), matcher));
    }

    private ChangeListener<Boolean> editListener() {
        return new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                submit.setDisable(newValue);
            }
        };
    }

    private <T> ChangeListener<T> matchListener(T toMatch, SimpleBooleanProperty matcher) {
        return new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                matcher.set(newValue.equals(toMatch));
            }
        };
    }

    public void initSecondParentMatchers() {
        SimpleBooleanProperty secondParentFirstNameMatch = new SimpleBooleanProperty(true);
        setListener(secondParentFirstNameInput, secondParentFirstNameMatch);

        SimpleBooleanProperty secondParentLastNameMatch = new SimpleBooleanProperty(true);
        setListener(secondParentLastNameInput, secondParentLastNameMatch);

        SimpleBooleanProperty secondParentAddressMatch = new SimpleBooleanProperty(true);
        setListener(secondParentAddressInput, secondParentAddressMatch);

        SimpleBooleanProperty secondParentPhoneNumberMatch = new SimpleBooleanProperty(true);
        setListener(secondParentPhoneNumberInput, secondParentPhoneNumberMatch);

        SimpleBooleanProperty[] matches = new SimpleBooleanProperty[] {secondParentAddressMatch, secondParentFirstNameMatch,secondParentLastNameMatch, secondParentPhoneNumberMatch};

        BooleanBinding secondMatches = new BooleanBinding() {
            {
                super.bind(matches);
            }
            @Override
            protected boolean computeValue() {
                for (SimpleBooleanProperty booleanProperty : matches) {
                    if (!booleanProperty.get()) {
                        return false;
                    }
                }
                return true;
            }
        };
        secondMatches.addListener(editListener());
    }

    public void setListController(ListController listController) {
        this.listController = listController;
    }

    public void setDisplayController(ChildDisplayController displayController) {
        this.displayController = displayController;
    }
}
