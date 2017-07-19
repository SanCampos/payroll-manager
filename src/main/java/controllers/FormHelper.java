package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import main.java.utils.DialogUtils;

/**
 * Created by thedr on 7/18/2017.
 */
public class FormHelper {
    
    public static void cancel(ActionEvent actionEvent, Stage stage) {
        if (DialogUtils.getConfirm("Confirm form cancellation", "If you cancel submitting this form, you would have to do it all over again. Do you wish to cancel")) {
            stage.close();
        }
    }
}
