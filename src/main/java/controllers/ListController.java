package main.java.controllers;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.Main;
import main.java.db.Database;
import main.java.models.Employee;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private ImageView profImg;
    @FXML private Button logout_button;

    @FXML private TableView<Employee> table;
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

        table.setItems(db.getEmployees());
        col_fname.setCellValueFactory(new PropertyValueFactory<Employee, String>("fName"));
        col_lname.setCellValueFactory(new PropertyValueFactory<Employee, String>("lName"));
        col_salary.setCellValueFactory(new PropertyValueFactory<Employee, String>("salary"));
        col_age.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("age"));

        table.setRowFactory((Callback<TableView<Employee>, TableRow<Employee>>) param -> {
            TableRow<Employee> row =  new TableRow<>();

            row.setOnMouseClicked(event -> {
                int id =  row.getItem().getId();
            });
            return row;
        });
    }

    private void disableReorder() {
        table.widthProperty().addListener((observable, oldValue, newValue) -> {
            TableHeaderRow row = ((TableHeaderRow) table.lookup("TableHeaderRow"));
            row.reorderingProperty().addListener((observable1, oldValue1, newValue1) -> row.setReordering(false));
        }); //Fuck you oracle
    }


    @FXML
    public void logout() throws SQLException {
        Stage stage = ((Stage) logout_button.getScene().getWindow());
        stage.close();
        Main.loginStage.show();
    }

    private void initAvatar() {
        Circle circle = new Circle(32.5, 32.5, 32.5); //DONT FUCKIN CHANGE THIS
        circle.setFill(Paint.valueOf("RED"));
        profImg.setClip(circle);
    }

    @FXML
    public void showSettings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/settings.fxml"));
        Scene scene = new Scene(root, 600, 400);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.show();
    }
}
