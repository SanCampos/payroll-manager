package main.java.controllers;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.Main;
import main.java.Settings.SettingsStage;
import main.java.db.Database;
import main.java.globalInfo.GlobalInfo;
import main.java.models.Employee;
import main.java.utils.ShapeUtils;

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

    //TO BE MOVED TO EXTERNAL CLASS
    private static Database db;

    @FXML
    public void initialize() {
        db = new Database();
        try {
            loadTableData();
        } catch (SQLException e){
            System.out.println("loading  failed");
        }
        initAvatar();
        disableReorder();
    }

    private void initAvatar() {
        profImg.setClip(ShapeUtils.getAvatarCircle());
        profImg.setImage(new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath()));
    }

    private void loadTableData() throws SQLException {
        db.init();

        table.setItems(db.getEmployees());
        col_fname.setCellValueFactory(new PropertyValueFactory<Employee, String>("fName"));
        col_lname.setCellValueFactory(new PropertyValueFactory<Employee, String>("lName"));
        col_salary.setCellValueFactory(new PropertyValueFactory<Employee, String>("salary"));
        col_age.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("age"));

        table.setRowFactory(param -> {
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

    @FXML
    public void showSettings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/settings.fxml"));
        SettingsStage stage = new SettingsStage();
        stage.setOnHidden((event) -> {
            SettingsStage settingsStage = ((SettingsStage) event.getSource());
            if (settingsStage.getChange()) {
                initialize();
            }
        });
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
