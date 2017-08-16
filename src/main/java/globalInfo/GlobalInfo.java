package main.java.globalInfo;

import javafx.scene.image.Image;
import main.java.db.Database;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by thedr on 6/14/2017.
 */
public class GlobalInfo {
    //Globally needed user info
    //IS THIS IMPORTANT I  WISH I HAD SOFTWARE ENGINEER TO  TALK TO
    private static String employeesImgDir = "C:\\imgs\\employees";
    private static Image currProfImg;

    private static String childrenImgDir = "C:\\imgs\\children";

    private static int userID;
    private static int prvlg_lvl;

    public static int getPrvlg_lvl() {
        return prvlg_lvl;
    }

    public static void setPrvlg_lvl(int prvlg_lvl) {
        GlobalInfo.prvlg_lvl = prvlg_lvl;
    }

    public static int getUserID() {
        return userID;
    }

    public static void setUserID(int id) {
        GlobalInfo.userID = id;
    }

    public static String getEmployeesImgDir() {
        return employeesImgDir;
    }

    public static void setEmployeesImgDir(String employeesImgDir) {
        GlobalInfo.employeesImgDir = employeesImgDir;
    }

    public static Image getCurrProfImg() {
        return currProfImg;
    }

    public static void setCurrProfImg(Image currProfImg) {
        GlobalInfo.currProfImg = currProfImg;
    }

    public static String getChildrenImgDir() {
        return childrenImgDir;
    }
}
