package main.java.customNodes;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.TextField;

/**
 * Created by Santi on 6/29/2017.
 */
public class PersistentPromptTextField extends TextField {

    public PersistentPromptTextField() {
        getStyleClass().add("input-prompt");

        textProperty().addListener(observable -> refreshPromptText());
    }

    private void refreshPromptText() {
        final String text = getText();

        if (text.isEmpty() || text == null) {
            getStyleClass().remove("no-prompt");
        } else if (!getStyleClass().contains("no-prompt")) {
            getStyleClass().add("no-prompt");
        }
    }
}
