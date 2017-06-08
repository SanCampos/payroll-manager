package main.java.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by thedr on 5/31/2017.
 */
public class Employee { //YES  YOU  DO

    private SimpleStringProperty fName;
    private SimpleStringProperty lName;
    private SimpleIntegerProperty age;
    private SimpleDoubleProperty salary;
    private SimpleIntegerProperty id;

    public Employee(String fName, String lName, int age, double salary, int id) {
        this.fName = new SimpleStringProperty(fName);
        this.lName = new SimpleStringProperty((lName));
        this.age   = new SimpleIntegerProperty(age);
        this.salary = new SimpleDoubleProperty(salary);
        this.id    = new SimpleIntegerProperty(id);
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

    public int getAge() {
        return age.get();
    }

    public SimpleIntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public double getSalary() {
        return salary.get();
    }

    public SimpleDoubleProperty salaryProperty() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
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
}
