package main.java.controllers;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.Main;
import main.java.Settings.SettingsStage;
import main.java.db.Database;
import main.java.globalInfo.GlobalInfo;
import main.java.models.Child;
import main.java.utils.DialogUtils;
import main.java.utils.ImageUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by thedr on 6/6/2017.
 */
public class ListController {

    @FXML private Text userName;
    @FXML private TextField searchBar;
    @FXML private VBox vBox;
    @FXML private ImageView profImg;
    @FXML private Button logout_button;

    @FXML private BorderPane borderPane;

    @FXML private TableView<Child> table;
    @FXML private TableColumn col_name;
    @FXML private TableColumn col_picture;

    //TO BE MOVED TO EXTERNAL CLASS
    private static Database db;

    private int currentLoadedChild;
    private Parent currentLoadedChildSceneRoot;
    
    private ListController itself;

    private FilteredList<Child> filteredChildren;

    @FXML private Button delete;

    @FXML
    public void initialize() {
        db = new Database();
        
        try {
            db.init();
            initTable();
            initSearchFunctionality();
        } catch (SQLException e){
            System.out.println("loading  failed");
        }
        initAvatar();
        disableReorder();
        disablePictureSort();
        currentLoadedChild = -1;
        userName.setText(GlobalInfo.getUserName());
        itself = this;
    }

    
    private void initSearchFunctionality() {
        //init search bar functionality
        searchBar.textProperty().addListener(((observable, oldValue, newValue) -> {
            String query = searchBar.getText();

            filteredChildren.setPredicate(child -> (query == null || query.isEmpty()) ||
                        (StringUtils.containsIgnoreCase(child.getCompleteName(), query))
            );
        }));
    }

    private void initAvatar() {
        profImg.setClip(ImageUtils.getAvatarCircle(profImg.getFitHeight()));
        Image value = GlobalInfo.getCurrProfImg();
        profImg.setImage(value);
    }

    public void initTable() throws SQLException {
        loadChildren();
        
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
                                    controller.setListRoot(vBox.getParent());
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

        col_picture.setCellValueFactory(new PropertyValueFactory<Child, Image>("image"));
        col_picture.setCellFactory(new Callback<TableColumn<Child, Image>, TableCell<Child, Image>>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<Child, Image>() {
                    ImageView imageView = new ImageView();
                    Image childImage;

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        Child child = (Child) getTableRow().getItem();

                        if (child == null || !StringUtils.containsIgnoreCase(child.getCompleteName(), searchBar.getText())) {
                            imageView.setImage(null);

                        } else if (item != null) {
                            childImage = ((Image) child.getImage());

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
        table.prefHeightProperty().bind(borderPane.heightProperty());
    }
    
    public void loadChildren() throws SQLException {
        db.init();
        Predicate<? super Child> filterPredicate = filteredChildren == null ? null : filteredChildren.getPredicate();
        filteredChildren = new FilteredList<>(db.getChildren(), filterPredicate);
        table.setItems(filteredChildren);
        delete.setDisable(table.getItems().size() <= 0);
        delete.setDisable(table.getItems().size() <= 0);
        table.getSelectionModel().select(0);
    }
    
    private void disablePictureSort() {
        col_picture.setSortable(false);
    }

    private void disableReorder() {
        table.skinProperty().addListener(((observable, oldValue, newValue) -> {
            TableHeaderRow header = (TableHeaderRow)table.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable1, oldValue1, newValue1) -> {
                header.setReordering(false);
            });
        }));//Fuck you oracle
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
    public void showChildForm(ActionEvent actionEvent)  {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Child> getChildren() {
        return table.getItems();
    }
    
    public void setQuery(String query) {
        searchBar.setText(query);
    }

    public void deleteChild(ActionEvent actionEvent) {
        if (DialogUtils.getConfirm("Child deletion confirmation", "Are you sure you want to delete this child? This cannot be undone"))
        {
            int id = table.getSelectionModel().getSelectedItem().getId();
            try {
                db.deleteChild(id);
                loadChildren();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
