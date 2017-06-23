package main.java.utils;

import javafx.scene.control.Alert;

/**
 * Created by thedr on 6/23/2017.
 */
public class DialogUtils {
    
    public static void showError(String title, String message) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle(title);
        errorDialog.setContentText(message);
        errorDialog.setHeaderText(null);
        errorDialog.showAndWait();
    }
}
