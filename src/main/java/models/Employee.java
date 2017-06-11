package main.java.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;

/**
 * Model class for interfacing with table view
 */
public class Employee {

    private SimpleStringProperty fName;
    private SimpleStringProperty lName;
    private SimpleIntegerProperty age;
    private SimpleStringProperty salary;
    private SimpleIntegerProperty id;

    private static DecimalFormat salaryFormat = new DecimalFormat("#,###.00");

    public Employee(String fName, String lName, int age, double salary, int id) {
        this.fName = new SimpleStringProperty(Character.toUpperCase(fName.charAt(0)) + fName.substring(1));
        this.lName = new SimpleStringProperty(Character.toUpperCase(lName.charAt(0)) + lName.substring(1));
        this.age   = new SimpleIntegerProperty(age);
        this.salary = new SimpleStringProperty(salaryFormat.format(salary));
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

    public String getSalary() {
        return salary.get();
    }

    public SimpleStringProperty salaryProperty() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary.set(salaryFormat.format(salary));
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
