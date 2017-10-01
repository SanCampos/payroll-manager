package main.java.customNodes.ChangeDetectingInputs;

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
}
