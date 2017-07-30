package main.java.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import org.apache.commons.collections4.BidiMap;

import java.io.File;
import java.util.Map;

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
    private SimpleStringProperty birth_date;
    private SimpleStringProperty admission_date;
    private SimpleStringProperty status;
    private SimpleStringProperty referrer;
    private SimpleIntegerProperty id;

    private SimpleObjectProperty<File> image;

    private SimpleMapProperty<ParentType, Parent> parents;

    public static class Parent {
        private SimpleStringProperty fName;
        private SimpleStringProperty lName;
        private SimpleStringProperty phoneNo;
        private SimpleStringProperty address;

        public Parent(String fName, String lName, String phoneNo, String address) {
            this.fName = new SimpleStringProperty(fName);
            this.lName = new SimpleStringProperty(lName);
            this.phoneNo = new SimpleStringProperty(phoneNo);
            this.address = new SimpleStringProperty(address);
        }

        public String getfName() {
            return fName.get();
        }

        public SimpleStringProperty fNameProperty() {
            return fName;
        }

        public void setfName(String fName) {
            this.fName.set(fName);
        }

        public String getlName() {
            return lName.get();
        }

        public SimpleStringProperty lNameProperty() {
            return lName;
        }

        public void setlName(String lName) {
            this.lName.set(lName);
        }

        public String getPhoneNo() {
            return phoneNo.get();
        }

        public SimpleStringProperty phoneNoProperty() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo.set(phoneNo);
        }

        public String getAddress() {
            return address.get();
        }

        public SimpleStringProperty addressProperty() {
            return address;
        }

        public void setAddress(String address) {
            this.address.set(address);
        }
    }

    public enum ParentType {
        MOTHER, FATHER
    }

    public Child(String fName, String lName, String nickname, String place_of_birth, String description, String gender, String birth_date, String admission_date, String status, String referrer, int id, File avatar, BidiMap<ParentType, Parent> parents) {
        this.fName = new SimpleStringProperty(fName);
        this.lName = new SimpleStringProperty(lName);
        this.nickname = new SimpleStringProperty(nickname);
        this.place_of_birth = new SimpleStringProperty(place_of_birth);
        this.description = new SimpleStringProperty(description);
        this.gender = new SimpleStringProperty(gender);
        this.birth_date = new SimpleStringProperty(birth_date);
        this.admission_date = new SimpleStringProperty(admission_date);
        this.status = new SimpleStringProperty(status);
        this.referrer = new SimpleStringProperty(referrer);
        this.id = new SimpleIntegerProperty(id);
        this.image = new SimpleObjectProperty<>(avatar);
        this.parents = new SimpleMapProperty<>();

        for (Parent parent : parents.values()) {
            addParent(parent, parents.getKey(parent));
        }
    }

    private void addParent(Parent parent, ParentType parentType) {
        parents.put(parentType, parent);
    }

    private void addFather(Parent father) {
        parents.put(ParentType.FATHER, father);
    }

    private void addMother(Parent mother) {
        parents.put(ParentType.MOTHER, mother);
    }

    public String getfName() {
        return fName.get();
    }

    public SimpleStringProperty fNameProperty() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName.set(fName);
    }

    public String getlName() {
        return lName.get();
    }

    public SimpleStringProperty lNameProperty() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName.set(lName);
    }

    public String getNickname() {
        return nickname.get();
    }

    public SimpleStringProperty nicknameProperty() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public String getPlaceOfBirth() {
        return place_of_birth.get();
    }

    public SimpleStringProperty place_of_birthProperty() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth.set(place_of_birth);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getGender() {
        return gender.get();
    }

    public SimpleStringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getBirthDate() {
        return birth_date.get();
    }

    public SimpleStringProperty birth_dateProperty() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date.set(birth_date);
    }

    public String getAdmissionDate() {
        return admission_date.get();
    }

    public SimpleStringProperty admission_dateProperty() {
        return admission_date;
    }

    public void setAdmission_date(String admission_date) {
        this.admission_date.set(admission_date);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getReferrer() {
        return referrer.get();
    }

    public SimpleStringProperty referrerProperty() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer.set(referrer);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public Object getImage() {
        return image.get();
    }

    public void setImage(SimpleObjectProperty<File> image) {
        this.image = image;
    }

    public String getCompleteName() {
        String firstName = getfName();
        String lastName = getlName();
        String nickname = getNickname();

        String placeholder = "PLACEHOLDER"; //Place holder for adding nickname if exists
        String complete = firstName + placeholder + lastName;

        //place nickname between first and last name if exists
        if (nickname.length() != 0){
            String nicknameString = " \"" + nickname + "\" ";
            complete =  complete.replace(placeholder, nicknameString);

        //if nickname does not exist
        } else {
            complete = complete.replace(placeholder, " ");
        }
        return complete;
    }
}
