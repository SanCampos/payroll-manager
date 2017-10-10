package main.java.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import main.java.customNodes.*;
import main.java.db.Database;
import main.java.db.DbSchema.*;
import main.java.globalInfo.ServerInfo;
import main.java.models.Child;
import main.java.utils.DialogUtils;
import main.java.utils.ImageUtils;
import main.java.utils.NodeUtils;
import main.java.utils.SocketUtils;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santi on 6/29/2017.
 */
public class ChildFormController extends FormHelper {
    
    @FXML
    private FormImageView childImageView;
    @FXML
    private Label imageName;
    
    @FXML
    private FormTextField firstNameInput;
    @FXML
    private FormTextField lastNameInput;
    @FXML
    private FormTextField nickNameInput;
    @FXML
    private FormTextField birthPlaceInput;
    @FXML
    private FormTextField referrerInput;
    
    @FXML
    private FormDatePicker birthDateInput;
    @FXML
    private FormDatePicker admissionDateInput;
    
    @FXML
    private Button submitBtn;
    
    @FXML
    private ComboBox childStatus;
    
    //TWO SCOOPS TWO GENDERS TWO TERMS
    @FXML
    private ToggleGroup genderToggleGroup;
    
    @FXML
    private FormTextArea childDescInput;
    @FXML
    private Label warnEmptyLabel;

    private FileInputStream slctdImgStrm;

    private String pathRef;
    
    private Parent nextParent;
    
    private ChildParentsController childParentsController;

    private ListController listController;
    
    private Child child;
    private SimpleBooleanProperty[] matches;
    private File uploadedImage;
    
    private ChildDisplayController displayController;
    private SimpleBooleanProperty imageMatches;

    @FXML
    public void initialize() throws FileNotFoundException {
        initGenderToggles();
        initChildStatusSelector();
        initDateValidationFor(birthDateInput, admissionDateInput);

        //Init default image for child
        //updateChosenImage(null);
        /*Init submit/next btn
        initNextBtn();
        birthDateInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (admissionDateInput.getValue() != null) {
                if (newValue.isAfter(admissionDateInput.getValue())) {
                    birthDateInput.setValue(admissionDateInput.getValue());
                }
            }
        });

        admissionDateInput.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (birthDateInput.getValue() != null) {
                if (newValue.isBefore(birthDateInput.getValue())) {
                    admissionDateInput.setValue(birthDateInput.getValue());
                }
            }
        }));*/
    }

    private void initGenderToggles() {
        genderToggleGroup.getToggles().get(0).setSelected(true);
    }

    private void initChildStatusSelector() {
        childStatus.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 2) {
                initSubmitBtn();
            } else if (child == null) {
                initNextBtn();
            }
        });
        childStatus.getSelectionModel().selectFirst();
    }

    private void initDateValidationFor(DatePicker older, DatePicker later) {
        ChangeListener<LocalDate> dateListener = (observable, oldValue, newValue) -> {
            if (later.getValue() != null) {
                if (older.getValue().isAfter(later.getValue())) {
                    older.setValue(later.getValue());
                }
            } else if (older.getValue() != null) {
                if (later.getValue().isBefore(older.getValue())) {
                    later.setValue(older.getValue());
                }
            }
        };
        older.valueProperty().addListener(dateListener);
        later.valueProperty().addListener(dateListener);
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
                prepareParentForm();
            }
            submitBtn.getScene().setRoot(nextParent);
            childParentsController.setPrevRoot(submitBtn.getParent());
            childParentsController.setChildFormController(this);
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.displayExceptionError(e, "Severe error!");
        }
    }
    
    private void prepareParentForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childParentsForm.fxml"));
        Parent root = loader.load();
        childParentsController = loader.getController();
        setNextParent(root);
    }
    
    private void initSubmitBtn() {
        submitBtn.setText("Submit");
        submitBtn.getStyleClass().remove("default");
        submitBtn.getStyleClass().add("submit");
        submitBtn.setOnAction(event -> submit(true));
    }
    
    @FXML
    public void cancel(ActionEvent actionEvent) {
         FormHelper.cancel(actionEvent, ((Stage) submitBtn.getScene().getWindow()));
    }


    /**
     * Submits the child with all of its respecitve information
     * @param notAddingParents if no parents will be added (i.e. only this form will be encountered)
     * @return id of child submitted, negative number if submission has failed
     */
    public int submit(boolean notAddingParents) {
        if (formIsIncomplete())
            return -1;
        
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

        //Retrieve record's ID for later use
        int id;
        
        try {
            Database db = new Database();
            db.init();

            if (child == null) { //add new child info
                id = db.addNewChild(firstName, lastName, nickName, place_of_birth, birthDate, childDesc, gender, referrer, status, admissionDate);

                if (uploadedImage != null) {
                    uploadImage(id, db);
                }

            } else { //update info of old child
                id = child.getId();
                db.updateChild(firstName, lastName, nickName, place_of_birth, birthDate, childDesc, gender, referrer, status, admissionDate, id);

                if (!imageMatches.getValue() && uploadedImage != null) {
                    uploadImage(id, db);
                }
            }

            //make table in the listController of children search for the updated child
            listController.setQuery(Child.getCompleteName(firstName, lastName, nickName));
            listController.loadChildrenData();

            //update child display info if updating old child
            if (displayController != null) displayController.setChild(listController.getChildren().get(0));

            if (notAddingParents) {
                firstNameInput.getScene().getWindow().hide();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtils.displayError("Error saving child data", "There was an error in saving all child data. Please try again!");
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.displayError("Error saving image", "There was an error saving the image of the child. " +
                    "All other data besides the image has been saved. Please attempt to add the child image in its own page.");
            return -1;
        } catch (Exception e) {
            return 1;
        }
        return id;
    }

    private void uploadImage(int id, Database db) throws SQLException, IOException {
        String path = SocketUtils.uploadImageto(ServerInfo.CHILD_IMAGE_REGISTER_PORT, uploadedImage, table_children.name, id);
        if (path != null) {
            db.updateImagePathOf(id, path, "children");
        }
        slctdImgStrm = new FileInputStream(uploadedImage);
    }

    private boolean formIsIncomplete() {
        //Clear warning labels
        warnEmptyLabel.setStyle("-fx-text-fill: transparent");
        
        //Indicates that form is incomplete
        boolean incomplete = false;
        
        //Fetches textfield nodes from root
        try {
            List<Node> textFields = NodeUtils.getAllNodesOf(childImageView.getParent(), new ArrayList<>(),
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
                    for (String id: ids) {
                        Label warning = ((Label) childImageView.getParent().lookup("#" + id));
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
        uploadedImage = chosen;
        if (chosen != null) {
            slctdImgStrm = new FileInputStream(chosen);
            imageName.setText(chosen.getName());
            childImageView.setImage(new Image(slctdImgStrm));
            slctdImgStrm = new FileInputStream(chosen);
        } else {
            childImageView.setImage(null);
        }
    }
    
    public void setNextParent(Parent nextParent) {
        this.nextParent = nextParent;
    }

    public void setListController(ListController listController) {
        this.listController = listController;
    }
    
    public void setChild(Child child) throws IOException {
        this.child = child;

        firstNameInput.setOrigValue(child.getfName());
        lastNameInput.setOrigValue(child.getlName());
        nickNameInput.setOrigValue(child.getNickname());

        childImageView.setOrigValue(((Image) child.getImage()));
        birthDateInput.setOrigValue(LocalDate.parse(child.getBirth_date()));
        admissionDateInput.setOrigValue(LocalDate.parse(child.getAdmission_date()));

        birthPlaceInput.setOrigValue(child.getPlace_of_birth());

        referrerInput.setOrigValue(child.getReferrer());

        genderToggleGroup.getToggles().get(child.getGender().equalsIgnoreCase("male") ? 0 : 1);
        for (Object o : childStatus.getItems())  {
            if (o.equals(child.getStatus())) {
                childStatus.getSelectionModel().select(childStatus.getItems().indexOf(o));
                break;
            }
        }
        childDescInput.setOrigValue(child.getDescription());
        Platform.runLater(this::initChangeDetection);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childParentsForm.fxml"));
        Parent root = loader.load();
        childParentsController = loader.getController();
        setNextParent(root);
        childParentsController.setParents(child.getParents(), child.getId());
        initSubmitBtn();
        submitBtn.setDisable(true);
    }
    
    private void initChangeDetection() {
        ChangeListener<Object> changeListener = new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,  Object newValue) {
                submitBtn.setDisable(formIsEdited());
            }

            private boolean formIsEdited() {
                for (Node n : firstNameInput.getParent().getChildrenUnmodifiable()) {
                    if (n instanceof ChangeDetectingInput) {
                        if (((ChangeDetectingInput) n).valueChanged())
                            return true;
                    }
                }
                return false;
            }
        };

        for (Node n : firstNameInput.getParent().getChildrenUnmodifiable()) {
            if (n instanceof ChangeDetectingInput) {
                ((ChangeDetectingInput) n).addOnChangeListener(changeListener);
            }
        }

        /* SimpleBooleanProperty firstNameMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty lastNameMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty nickNameMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty birthPlaceMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty birthDateMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty admissionDateMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty descriptionMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty referrerMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty genderMatches = new SimpleBooleanProperty(true);
        SimpleBooleanProperty statusMatches = new SimpleBooleanProperty(true);
        imageMatches = new SimpleBooleanProperty(true);
        
        firstNameInput.textProperty().addListener(matchingListener(child.getfName(), firstNameMatches));
        lastNameInput.textProperty().addListener(matchingListener(child.getlName(), lastNameMatches));
        nickNameInput.textProperty().addListener(matchingListener(child.getNickname(), nickNameMatches));
        birthPlaceInput.textProperty().addListener(matchingListener(child.getPlace_of_birth(), birthPlaceMatches));
        birthDateInput.valueProperty().addListener(matchingListener(LocalDate.parse(child.getBirth_date()), birthDateMatches));
        admissionDateInput.valueProperty().addListener(matchingListener(LocalDate.parse(child.getAdmission_date()), admissionDateMatches));
        childDescInput.textProperty().addListener(matchingListener(child.getDescription(), descriptionMatches));
        referrerInput.textProperty().addListener(matchingListener(child.getReferrer(), referrerMatches));
        genderToggleGroup.selectedToggleProperty().addListener(matchingListener(child.getGender().equalsIgnoreCase("male") ? 0 : 1, genderMatches));
        childStatus.getSelectionModel().selectedItemProperty().addListener(matchingListener(child.getStatus(), statusMatches));
        childImageView.imageProperty().addListener(matchingListener(((Image) child.getImage()), imageMatches));
        matches = new SimpleBooleanProperty[]{firstNameMatches, lastNameMatches, nickNameMatches, birthPlaceMatches, birthDateMatches, admissionDateMatches, descriptionMatches, referrerMatches, genderMatches, statusMatches, imageMatches};

        BooleanBinding nothingEdited = new BooleanBinding() {
            {
                super.bind(matches);
            }
            @Override
            protected boolean computeValue() {
                for (SimpleBooleanProperty booleanProperty : matches) {
                    if (!booleanProperty.getValue())
                        return false;
                }
                return true;
            }
        };
        nothingEdited.addListener(((observable, oldValue, newValue) -> submitBtn.setDisable(newValue))); */
    }

    //black magic
    private <T> ChangeListener<T> matchingListener(T toMatch, SimpleBooleanProperty matcher) {
        return new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                if (newValue instanceof Toggle)  {
                    matcher.set(((Object) genderToggleGroup.getToggles().indexOf(newValue)).equals(toMatch));
                } else if (newValue instanceof Image) {
                    matcher.set(ImageUtils.equals(((Image) newValue), ((Image) toMatch)));
                } else {
                    boolean matches = newValue.equals(toMatch);
                    matcher.set(matches);
                }
            }

        };
    }
    
    public void setDisplayController(ChildDisplayController displayController) {
        this.displayController = displayController;
    }
}




