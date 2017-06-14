package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.IO.FilePaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static main.java.controllers.ControllerUtils.getAvatarCircle;

/**
 * Created by thedr on 6/14/2017.
 */
public class SettingsController {

    @FXML ImageView prof_img;

    @FXML
    public void initialize() {
        initAvatar();
    }

    private void initAvatar() {
        prof_img.setClip(getAvatarCircle());
    }

    public void getPicFile() {
        //Makes user choose a picture file and copies that file to local picture directory

        //Init fileChooser (what kind of name is that)
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.jpg, *.gif, *.png)",
                                                                         "*.png", "*.jpg", "*.gif")); // possible refactor?

        try {
            //Create file objects for chosen and created file and load stream
            File master = chooser.showOpenDialog(prof_img.getScene().getWindow());
            File created = new File(FilePaths.localImgPath + "\\" + master.getName());
            InputStream stream = new FileInputStream(master);

            //Creates img and respective dir if !exists
            if (!(created.exists() && created.isFile())) {
                created.getParentFile().mkdirs();
                created.createNewFile();
            }

            //Finally copies the bytes of chosen file to the created one, overwriting if necessary
            Files.copy(stream, Paths.get(created.getPath()), REPLACE_EXISTING);
        } catch (IOException e) {

        }
    }
}
