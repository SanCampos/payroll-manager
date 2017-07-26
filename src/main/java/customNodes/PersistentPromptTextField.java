package main.java.customNodes;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;

/**
 * Created by Santi on 6/29/2017.
 */
public class PersistentPromptTextField extends TextField {

    private IntegerProperty maxLength;

    public PersistentPromptTextField() {
        String cssPath = this.getClass().getResource("/css/persistent-prompt.css").toExternalForm();
        getStylesheets().add(cssPath);
        getStyleClass().add("input-prompt");
    
        textProperty().addListener(observable -> refreshPromptText());
        maxLength = new SimpleIntegerProperty(-1);
    }

    public int getMaxLength() {
        return maxLength.get();
    }

    public IntegerProperty maxLengthProperty() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength.set(maxLength);
    }

    @Override
    public void replaceText(int start, int end, String insertedText) {
        if (getMaxLength() == -1) {
            super.replaceText(start, end, insertedText);
        }

        String combinedText = getText().substring(0, start) + insertedText + getText().substring(end);
        int charDiff = combinedText.length() - getMaxLength();
        if (charDiff <= 0) {
            super.replaceText(start, end, insertedText);
        } else {
            String cutInsertedText = insertedText.substring(0, insertedText.length() - charDiff);
            super.replaceText(start, end, cutInsertedText);
        }
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
