package main.java.controllers;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import main.java.Main;
import main.java.db.Database;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private ImageView profImg;
    @FXML private Button logout;
    @FXML private TableView table;

    @FXML
    public void initialize() {
        initAvatar();
        disableReorder();

        Database db = new Database();

        try {
            db.init();

            for (Map<String, String> row : db.getTableData()) {

            }

        } catch (SQLException e) {
            System.out.println("Couldn't  connect");
        }

    }

    private void disableReorder() {
        TableColumn[] columns = new TableColumn[]{new TableColumn("First Name"), new TableColumn("Last name"),
                                                  new TableColumn("Salary"), new TableColumn("Age")};
        table.getColumns().addListener(new ListChangeListener() {
            private boolean suspended;

            @Override
            public void onChanged(Change c) {
                c.next();
                if (c.wasReplaced() && !this.suspended) {
                    suspended = true;
                    table.getColumns().setAll(columns);
                    suspended = false;
                }
            }
        });
    }

    @FXML
    public void logout() throws SQLException {
        Stage stage = ((Stage) logout.getScene().getWindow());
        stage.close();
        Main.loginStage.show();
    }

    private void initAvatar() {
        Circle circle = new Circle(32.5, 32.5, 32.5); //DONT FUCKIN CHANGE THIS
        circle.setFill(Paint.valueOf("RED"));
        profImg.setClip(circle);
    }

}
