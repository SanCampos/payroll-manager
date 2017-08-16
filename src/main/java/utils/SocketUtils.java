package main.java.utils;

import main.java.db.Database;
import main.java.db.DbSchema;
import main.java.globalInfo.GlobalInfo;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

import static java.lang.Math.toIntExact;

/**
 * Created by Santi on 8/16/2017.
 */
public class SocketUtils {

    public static File uploadImageto(int portNumber, File image, String tableName, int entityID) {
        try (Socket socket = new Socket("127.0.0.1", portNumber);
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

            //actual image data
            byte[] bytes = new byte[toIntExact(imageSize)];
            int read;
            while ((read = imageStream.read(bytes)) > 0) {
                out.write(bytes, 0, read);
            }

            String filePath = in.readUTF();

            Database db = new Database();
            db.init();
            db.updateImageOf(entityID, filePath.replace("\\", "\\\\"), tableName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return image;
    }
}