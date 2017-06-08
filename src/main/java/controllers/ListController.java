package main.java.controllers;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import main.java.Main;
import main.java.db.Database;
import main.java.models.Employee;

import java.sql.SQLException;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private ImageView profImg;
    @FXML private Button logout;

    @FXML private TableView table;
    @FXML private TableColumn col_fname;
    @FXML private TableColumn col_lname;
    @FXML private TableColumn col_salary;
    @FXML private TableColumn col_age;


    @FXML
    public void initialize() {
        try {
            loadTableData();
        } catch (SQLException e){
            System.out.println("loading  failed");
        }

        initAvatar();
        disableReorder();
    }

    private void loadTableData() throws SQLException {
        Database db = new Database();
        db.init();

        col_fname.setCellValueFactory(new PropertyValueFactory<Employee, String>("fName"));
        col_lname.setCellValueFactory(new PropertyValueFactory<Employee, String>("lName"));
        col_salary.setCellValueFactory(new PropertyValueFactory<Employee, String>("salary"));
        col_age.setCellValueFactory(new PropertyValueFactory<Employee, String>("age"));
        table.setItems(db.getEmployees());
    }

    private void disableReorder() {
        ObservableList columns = table.getColumns();

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
