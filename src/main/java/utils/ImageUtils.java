package main.java.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.shape.Circle;

/**
 * Created by thedr on 6/15/2017.
 */
public class ImageUtils {

    public static boolean equals(Image first, Image second) {
        //Checks if two images are identical pixel-for-pixel

        double height = first.getHeight();
        double width = second.getWidth();

        PixelReader fReader = first.getPixelReader();
        PixelReader sReader = second.getPixelReader();

        if (sReader == null || fReader == null) return false;

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (fReader.getArgb(j, i) != sReader.getArgb(j, i))
                    return false;

        return true;
    }

    public static Circle getAvatarCircle(double diameter) {
        return new Circle(diameter/2, diameter/2, diameter/2);
    }
}
