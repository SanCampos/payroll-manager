package main.java.customNodes.ChangeDetectingInputs;

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
}
