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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.java.models.Child;
import main.java.utils.DragResizerXY;
import main.java.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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
    
    private ListController listController;
    
    private boolean changeMade;

    @FXML
    public void initialize() {
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
    
    public void setChild(Child child) {
        childImage.setImage(new Image("file:///" + ((File) child.getImage()).getAbsoluteFile()));
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
            Text parentName = new Text(parent.getfName() + " "  + parent.getlName() + " \n");
            childParents.getChildren().add(parentName);
        }
    }
    
    public void setListRoot(Parent listRoot) {
        this.listRoot = listRoot;
    }
    
    public void goBackToList() {
        if (changeMade)  {
            try {
                listController.initTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        childAdmissionAge.getScene().setRoot(listRoot);
    }
    
    public void setListController(ListController listController) {
        this.listController = listController;
    }
}
