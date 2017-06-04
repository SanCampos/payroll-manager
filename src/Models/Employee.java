package Models;

/**
 * Created by thedr on 5/31/2017.
 */
public class Employee {

    private String fName;
    private String lName;
    private int age;
    private double salary;
    private int id;

    private Employee() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    private void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    private void setlName(String lName) {
        this.lName = lName;
    }

    public int getAge() {
        return age;
    }

    private void setAge(int age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    private void setSalary(double salary) {
        this.salary = salary;
    }

    public static class Builder { // FOR EDUCATIONAL PURPOSES REMOVE UPON COMPLETION
        private final Employee employee;

        public Builder() {
            employee = new Employee();
        }

        public Builder addfName(String fName) {
            employee.setfName(fName);
            return this;
        }

        public Builder addlName(String lName) {
            employee.setlName(lName);
            return this;
        }

        public Builder addSalary(double salary) {
            employee.setSalary(salary);
            return this;
        }

        public Builder addAge(int age) {
            employee.setAge(age);
            return this;
        }

        public Builder addId(int id) {
            employee.setId(id);
            return this;
        }

        public Employee create() {
            return employee;
        }
    }
}
