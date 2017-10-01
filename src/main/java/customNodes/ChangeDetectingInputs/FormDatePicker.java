package main.java.customNodes.ChangeDetectingInputs;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class FormDatePicker extends DatePicker implements ChangeDetectingInput<LocalDate> {

    private LocalDate origValue;

    @Override
    public void setOrigValue(LocalDate origValue) {
        setValue(origValue);
        this.origValue = origValue;
    }

    @Override
    public boolean valueChanged() {
        return origValue.equals(getValue());
    }
}
