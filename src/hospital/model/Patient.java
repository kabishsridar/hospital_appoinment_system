package hospital.model;

/**
 * Concrete Patient class inheriting from Person.
 */
public class Patient extends Person {
    private int age;
    private String gender;

    public Patient(String id, String name, String phone, String email, int age, String gender) {
        super(id, name, phone, email);
        this.age = age;
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getRoleDescription() {
        return "Patient [Age: " + age + ", Gender: " + gender + "]";
    }
}
