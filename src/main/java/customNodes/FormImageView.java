package main.java.customNodes;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.utils.ImageUtils;

public class FormImageView extends ImageView implements ChangeDetectingInput<Image> {

    private Image origValue;

    @Override
    public void setOrigValue(Image origValue) {
        setImage(origValue);
        this.origValue = origValue;
    }

    @Override
    public boolean valueChanged() {
        return ImageUtils.equals(getImage(), origValue);
    }

    @Override
    public void addOnChangeListener(ChangeListener<Image> changeListener) {
        imageProperty().addListener(changeListener);
    }
}
