package main.java.controllers;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

    private int currentLoadedChild;
    private Parent currentLoadedChildSceneRoot;
    
    private ListController itself;

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
        currentLoadedChild = -1;
        itself = this;
    }

    private void initAvatar() {
        profImg.setClip(ImageUtils.getAvatarCircle(profImg.getFitHeight()));
        Image value = new Image("file:///" + GlobalInfo.getCurrProfImg().getAbsolutePath());
        profImg.setImage(value);
    }

    public void initTable() throws SQLException {
        db.init();

        table.setRowFactory(new Callback<TableView<Child>, TableRow<Child>>() {
            @Override
            public TableRow<Child> call(TableView<Child> param) {
                TableRow<Child> row = new TableRow<>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                            try {
                                if (currentLoadedChild != row.getItem().getId()) {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childDisplay.fxml"));
                                    currentLoadedChildSceneRoot = loader.load();
                                    ChildDisplayController controller = loader.getController();
                                    controller.setChildIndex(table.getItems().indexOf(row.getItem()));
                                    controller.setChild(row.getItem());
                                    controller.setListController(itself);
                                    controller.setListRoot(table.getParent());
                                }
                                    profImg.getScene().setRoot(currentLoadedChildSceneRoot);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                return row;
            }});

        table.setItems(db.getChildren());
        col_picture.setCellValueFactory(new PropertyValueFactory<Child, File>("image"));
        col_picture.setCellFactory(new Callback<TableColumn<Child, File>, TableCell<Child, File>>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<Child, File>() {
                    ImageView imageView = new ImageView();
                    Image childImage;

                    @Override
                    protected void updateItem(File item, boolean empty) {
                        if (item != null) {
                            childImage = new Image("file:///" + item.getAbsolutePath());

                            imageView.setImage(childImage);
                            imageView.setFitHeight(65);
                            imageView.setFitWidth(65);
                            imageView.setClip(ImageUtils.getAvatarCircle(imageView.getFitHeight()));
                            HBox hBox = new HBox(imageView);
                            hBox.setAlignment(Pos.CENTER);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });



        col_name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Child, String>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Child, String> param) {
                Child child = param.getValue();
                String complete = child.getCompleteName();
                return new SimpleStringProperty(complete);
            }
        });

        col_name.setCellFactory(new Callback<TableColumn<Child, String>, TableCell<Child, String>>() {
            @Override
            public TableCell call(TableColumn<Child, String> param) {
                return new TableCell<Child, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        setText(item);
                        setAlignment(Pos.CENTER);
                    }
                };
            }
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

    @FXML
    public void showChildForm(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childForm.fxml"));
        Parent root = loader.load();
        ChildFormController controller = loader.getController();
        controller.setListController(this);
        Scene scene = new Scene(root, 575, 675);
        scene.getStylesheets().add(getClass().getResource("/css/persistent-prompt.css").toExternalForm());

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Add child");
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }
    
    public void updateChildOf(ChildDisplayController displayController) {
        int index = displayController.getChildIndex();
        displayController.setChild(table.getItems().get(index));
    }
}
