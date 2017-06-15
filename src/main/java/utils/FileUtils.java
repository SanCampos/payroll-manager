package main.java.utils;

/**
 * Created by thedr on 6/15/2017.
 */
public class FileUtils {

    public static String getFileName(String path) {
        int lastIndex = path.lastIndexOf('\\');
        if (lastIndex < 0) lastIndex = path.lastIndexOf('/');
        return path.substring(lastIndex+1);
    }
}
