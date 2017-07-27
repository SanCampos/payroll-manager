package main.java.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.java.models.Child;
import main.java.utils.ImageUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;

/**
 * Created by Santi on 7/27/2017.
 */
public class ChildDisplayController {

    @FXML Button backButton;

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

    @FXML
    public void initialize() {
        childImage.setClip(ImageUtils.getAvatarCircle(childImage.getFitHeight()));
        bannerRect.widthProperty().bind(anchorPane.widthProperty());
        final Rectangle centerClip = new Rectangle();
        anchorPane.setClip(centerClip);
        anchorPane.layoutBoundsProperty().addListener(((observable, oldValue, newValue) -> {
            centerClip.setWidth(newValue.getWidth());
            centerClip.setHeight(newValue.getHeight());
        }));
        /*(Platform.runLater(() -> ((Stage) childImage.getScene().getWindow())
                .maximizedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        anchorPane.setMaxWidth(anchorPane.getPrefWidth());
                        anchorPane.setMaxHeight(anchorPane.getPrefHeight());
                    } else {
                        anchorPane.setMaxHeight(Double.MAX_VALUE);
                        anchorPane.setMaxWidth(Double.MAX_VALUE);
                    }
                })); */

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
    }

}
