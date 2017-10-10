package main.java.customNodes;

import javafx.beans.value.ChangeListener;

public class FormTextField extends PersistentPromptTextField implements ChangeDetectingInput<String> {

    private String origText;

    @Override
    public void setOrigValue(String origValue) {
        setText(origValue);
        origText = origValue;
    }

    @Override
    public boolean valueChanged() {
        return getText().equals(origText);
    }

    @Override
    public void addOnChangeListener(ChangeListener<String> changeListener) {
        textProperty().addListener(changeListener);
    }
}
