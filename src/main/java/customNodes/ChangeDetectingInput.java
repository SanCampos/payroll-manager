package main.java.customNodes;


import javafx.beans.value.ChangeListener;

//
public interface ChangeDetectingInput<T> {

    void setOrigValue(T origValue);

    boolean valueChanged();

    void addOnChangeListener(ChangeListener<T> changeListener);
}
