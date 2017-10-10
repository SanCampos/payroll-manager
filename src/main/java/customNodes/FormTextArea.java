package main.java.customNodes;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextArea;

public class FormTextArea extends TextArea implements ChangeDetectingInput<String> {
    private String origValue;

    @Override
    public void setOrigValue(String origValue) {
        this.origValue = origValue;
    }

    @Override
    public boolean valueChanged() {
        return getText().equals(origValue);
    }

    @Override
    public void addOnChangeListener(ChangeListener<String> changeListener) {
        textProperty().addListener(changeListener);
    }
}
