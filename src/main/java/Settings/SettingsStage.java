package main.java.Settings;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

/**
 * Created by thedr on 6/26/2017.
 */
public class SettingsStage extends Stage {
    
    private SimpleBooleanProperty settingsChanged;
    
    public SettingsStage() {
        super();
        settingsChanged = new SimpleBooleanProperty(false);
    }
    
    public void setChange(boolean changed) {
        settingsChanged.set(changed);
    }

    public boolean getChange() {
        return settingsChanged.getValue();
    }
}
