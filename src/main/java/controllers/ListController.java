package main.java.controllers;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.Main;
import main.java.Settings.SettingsStage;
import main.java.db.Database;
import main.java.globalInfo.GlobalInfo;
import main.java.models.Child;
import main.java.utils.ImageUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private ImageView profImg;
    @FXML private Button logout_button;

    @FXML private TableView<Child> table;
    @FXML private TableColumn col_name;
    @FXML private TableColumn col_picture;

    //TO BE MOVED TO EXTERNAL CLASS
    private static Database db;

    @FXML
    public void initialize() {
        db = new Database();
        try {
            initTable();
        } catch (SQLException e){
            System.out.println("loading  failed");
        }
        initAvatar();
        disableReorder();
    }

    private void initAvatar() {
        profImg.setClip(ImageUtils.getAvatarCircle());
        profImg.setImage(new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath()));
    }

    private void initTable() throws SQLException {
        db.init();

        table.setRowFactory(param -> {
            TableRow<Child> row =  new TableRow<>();

            row.setOnMouseClicked(event -> {
                int id =  row.getItem().getId();
            });
            return row;
        });

        table.setItems(db.getChildren());
        col_picture.setCellValueFactory(new PropertyValueFactory<>("image"));
        col_picture.setCellFactory(new Callback<TableColumn<Child, Image>, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell<Child, Image> cell = new TableCell<Child, Image>() {
                    ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        imageView.setClip(ImageUtils.getAvatarCircle());
                        imageView.setImage(new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath()));

                        HBox hBox = new HBox();
                        hBox.setPrefHeight(35);
                        hBox.setSpacing(30);
                        hBox.setPrefWidth(35);
                        hBox.getChildren().add(imageView);
                        setGraphic(hBox);
                    }
                };
                return cell;
            }
        });

        col_name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Child, String>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Child, String> param) {
                Child child = param.getValue();
                String firstName = child.getfName();
                String lastName = child.getlName();
                String nickname = child.getNickname();
                return new SimpleStringProperty(firstName + " \"" + nickname + "\" " + lastName.replace(" \" \" "," "));
            }
        });

        col_name.setStyle("-fx-alignment: CENTER");
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

    @FXML
    public void showChildForm(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/childForm.fxml"));

        Scene scene = new Scene(root, 575, 675);
        scene.getStylesheets().add(getClass().getResource("/css/persistent-prompt.css").toExternalForm());

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Add child");
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }
}
