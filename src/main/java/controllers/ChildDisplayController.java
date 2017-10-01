package main.java.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.models.Child;
import main.java.utils.DragResizerXY;
import main.java.utils.ImageUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

/**
 * Created by Santi on 7/27/2017.
 */
public class ChildDisplayController {
    //banner
    @FXML ImageView childImage;
    @FXML Label childName;

    //first column
    @FXML Label childFirstName;
    @FXML Label childLastName;
    @FXML Label childAge;
    @FXML Label childBirthDate;
    @FXML Label childBirthPlace;
    @FXML Label childDescription;

    //second column
    @FXML Label childStatus;

    @FXML TextFlow childParents;
    @FXML Button parentsButton;

    @FXML Label childAdmissionAge;
    @FXML Label childAdmissionDate;
    @FXML Label childReferrer;

    @FXML Rectangle bannerRect;

    @FXML AnchorPane anchorPane;

    @FXML HBox issuePlaceHolder;
    
    @FXML Parent listRoot;
    
    @FXML StackPane editPane;
    
    private ListController listController;
    
    private boolean changeMade;
    
    private Child child;
    
    private int childIndex;
    
    @FXML
    public void initialize() {
        moveEditButttonRightOfName();
        changeMade = false;
        childImage.setClip(ImageUtils.getAvatarCircle(childImage.getFitHeight()));
        bannerRect.widthProperty().bind(anchorPane.widthProperty());
        final Rectangle centerClip = new Rectangle();
        anchorPane.setClip(centerClip);
        anchorPane.layoutBoundsProperty().addListener(((observable, oldValue, newValue) -> {
            centerClip.setWidth(newValue.getWidth());
            centerClip.setHeight(newValue.getHeight());
        }));
        issuePlaceHolder.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                    System.out.println(event.getX());
                }
        });
        DragResizerXY.makeResizable(issuePlaceHolder, true);
    }
    
    private void moveEditButttonRightOfName() {
        Platform.runLater(() -> {
            double topAnchor = AnchorPane.getTopAnchor(childName);
            double leftAnchor = AnchorPane.getLeftAnchor(childName) + 5;
            double length = childName.getWidth();
            AnchorPane.setTopAnchor(editPane, topAnchor);
            AnchorPane.setLeftAnchor(editPane, leftAnchor + length);
        });
    }
    
    public void setChild(Child child) {
        this.child = child;
        
        childImage.setImage(((Image) child.getImage()));
        childName.setText(child.getCompleteName());
        
        childFirstName.setText(child.getfName());
        childLastName.setText(child.getlName());
        childAge.setText(String.valueOf(Period.between(LocalDate.parse(child.getBirthDate()), LocalDate.now()).getYears()));
        childBirthDate.setText(child.getBirthDate());
        childBirthPlace.setText(child.getPlaceOfBirth());
        childDescription.setText(child.getDescription());
        
        childStatus.setText(child.getStatus());
        
        childAdmissionAge.setText(String.valueOf(Period.between(LocalDate.parse(child.getAdmissionDate()), LocalDate.now()).getYears()));
        childAdmissionDate.setText(child.getAdmissionDate());
        childReferrer.setText(child.getReferrer());
        
        for (Child.Parent parent : child.getParents()) {
            Text parentName = new Text(parent.getfName() + " " + parent.getlName() + " \n");
            childParents.getChildren().add(parentName);
        }
        
        moveEditButttonRightOfName();
    }
    
    public void setListRoot(Parent listRoot) {
        this.listRoot = listRoot;
    }
    
    public void goBackToList() {
        if (changeMade)  {
            try {
                listController.loadChildrenData();
                listController.initTableUI();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        childAdmissionAge.getScene().setRoot(listRoot);
    }
    
    public void setListController(ListController listController) {
        this.listController = listController;
    }
    
    public void editChild() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childForm.fxml"));
            Stage stage = new Stage();
            Parent root = loader.load();
            Scene scene = new Scene(root, 575, 675);
            ChildFormController controller = loader.getController();
            controller.setListController(listController);
            controller.setChild(child);
            controller.setDisplayController(this);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showParentInfo(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childParentsForm.fxml"));
            Parent root = loader.load();
            ChildParentsController controller = loader.getController();
            controller.setParents(child.getParents(), child.getId());
            controller.setListController(listController);
            controller.setChildName(child.getCompleteName());
            Stage fuck = new Stage();
            Scene ffuuuuuucckk = new Scene(root, 575, 675);
            fuck.setScene(ffuuuuuucckk);
            controller.setDisplayController(this);
            fuck.initModality(Modality.APPLICATION_MODAL);
            fuck.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }
    
    public int getChildIndex() {
        return childIndex;
    }
}
