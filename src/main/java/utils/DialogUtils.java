package main.java.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.lang.management.BufferPoolMXBean;
import java.util.Optional;

/**
 * Created by thedr on 6/23/2017.
 */

/**
 * Abstracts the usage of AlertDialogs into one-liner methods
 */


public class DialogUtils {
    
    /**
     * Creates an alert according to specified parameters and returns it for usage
     * @param alertType Type of alert to be used (CONFIRMATION, ERROR, ETC)
     * @param title Title of dialog
     * @param message Message of dialog
     * @return Alert object for usage as dialog
     */
    private static Alert makeDialog(Alert.AlertType alertType, String title, String message) {
        Alert dialog = new Alert(alertType);
        dialog.setTitle(title);
        dialog.setContentText(message);
        dialog.setHeaderText(null);
        return dialog;
    }
    
    /**
     * Generates an error notification for the user
     * @param title refer to 'makeDialog()'
     * @param message refer to 'makeDialog()'
     */
    public static void displayError(String title, String message) {
        Alert errorDialog = makeDialog(Alert.AlertType.ERROR, title, message);
        errorDialog.showAndWait();
    }
    
    /**
     * Generates a confirmation AlertDialog and returns the user's response
     * @param title refer to 'makeDialog()'
     * @param message refer to 'makeDialog()'
     * @return User response to confirmation
     */
    public static boolean getConfirm(String title, String message) {
        Alert confirmDialog = makeDialog(Alert.AlertType.CONFIRMATION, title, message);
    
        Optional<ButtonType> result = confirmDialog.showAndWait();
        
        return result.get() == ButtonType.OK;
    }
}
