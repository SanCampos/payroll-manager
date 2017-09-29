package main.java.utils;

import javafx.scene.image.Image;
import main.java.globalInfo.ServerInfo;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.Socket;

import static java.lang.Math.toIntExact;

/**
 * Created by Santi on 8/16/2017.
 */
public class SocketUtils {

    public static String uploadImageto(int portNumber, File image, String tableName, int entityID) {

        try (Socket socket = new Socket(ServerInfo.SERVER_IP, portNumber);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            long imageSize = image.length();

            if (imageSize > Integer.MAX_VALUE) {
                DialogUtils.displayError("Image upload error!", "Your image size is too large! Please choose an image smaller than 2GB");
                return null;
            }

            //write ID
            out.write(entityID);

            //length of extension
            char[] extensionChars = FilenameUtils.getExtension(image.getAbsolutePath()).toCharArray();
            out.write(extensionChars.length);

            //actual extension
            for (char c : extensionChars) {
                out.write((int) c);
            }

            out.writeUTF(String.valueOf(imageSize));

            FileInputStream imageStream = new FileInputStream(image);

            try (OutputStream imageOut = socket.getOutputStream()) {
                //actual image data
                byte[] bytes = new byte[toIntExact(imageSize)];
                int read;
                while ((read = imageStream.read(bytes)) > 0) {
                    imageOut.write(bytes, 0, read);
                }


            String filePath = in.readUTF();
            return filePath;
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image receiveImageFrom(int portNumber, int entityID) {
        try (Socket socket = new Socket(ServerInfo.SERVER_IP, portNumber);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            //send user info
            out.write(entityID);

            //error handling (???)
            if (in.available() == 1) {
                System.err.println("ERROR RETRIEVING USER IMAGE");
                return null;
            }

            //int fileSize = Integer.parseInt(in.readUTF());

            return new Image(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
