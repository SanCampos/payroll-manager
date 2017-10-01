package main.java.customNodes.ChangeDetectingInputs;

//
public interface ChangeDetectingInput<T> {

    void setOrigValue(T origValue);

    boolean valueChanged();

}
