package main.java.customNodes.ChangeDetectingInputs;

import main.java.customNodes.PersistentPromptTextField;

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
}
