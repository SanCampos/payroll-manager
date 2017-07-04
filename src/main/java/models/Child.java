package main.java.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by thedr on 7/4/2017.
 */
public class Child {
    
    private SimpleStringProperty fName;
    private SimpleStringProperty lName;
    private SimpleStringProperty nickname;
    private SimpleStringProperty place_of_birth;
    private SimpleStringProperty description;
    private SimpleStringProperty gender;
    
    public Child(String fName, String lName, String nickname, String place_of_birth, String description, String gender) {
        this.fName.set(fName);
        this.lName.set(lName);
        this.nickname.set(nickname);
        this.place_of_birth.set(place_of_birth);
        this.description.set(description);
        this.gender.set(gender);
    }
    
    public String getfName() {
        return fName.get();
    }
    
    public SimpleStringProperty fNameProperty() {
        return fName;
    }
    
    public String getlName() {
        return lName.get();
    }
    
    public SimpleStringProperty lNameProperty() {
        return lName;
    }
    
    public String getNickname() {
        return nickname.get();
    }
    
    public SimpleStringProperty nicknameProperty() {
        return nickname;
    }
    
    public String getPlace_of_birth() {
        return place_of_birth.get();
    }
    
    public SimpleStringProperty place_of_birthProperty() {
        return place_of_birth;
    }
    
    public String getDescription() {
        return description.get();
    }
    
    public SimpleStringProperty descriptionProperty() {
        return description;
    }
    
    public String getGender() {
        return gender.get();
    }
    
    public SimpleStringProperty genderProperty() {
        return gender;
    }
}
