package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import main.java.models.Child;

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
