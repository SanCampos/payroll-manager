package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private ImageView profImg;

    @FXML
    public void initialize() {
        Circle circle = new Circle(32.5, 32.5, 32.5); //DONT FUCKIN CHANGE THIS
        circle.setFill(Paint.valueOf("RED"));
        profImg.setClip(circle);
    }
}
