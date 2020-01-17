package ro.jademy.contactlist.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User implements Comparable<User> {

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

    private Map<String, PhoneNumber> phoneNumbers;
    private Address address;

    private String jobTitle;
    private Company company;

    private boolean isFavorite;

    public User(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, Address address, String jobTitle, Company company, boolean isFavorite) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.address = address;
        this.jobTitle = jobTitle;
        this.company = company;
        this.isFavorite = isFavorite;

    }

    public User(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, Address address, boolean isFavorite) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.address = address;
        this.isFavorite = isFavorite;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isFavorite == user.isFavorite &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(age, user.age) &&
                Objects.equals(phoneNumbers, user.phoneNumbers) &&
                Objects.equals(address, user.address) &&
                Objects.equals(jobTitle, user.jobTitle) &&
                Objects.equals(company, user.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, age, phoneNumbers, address, jobTitle, company, isFavorite);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Map<String, PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<String, PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName: '" + firstName + '\'' +
                ", lastName: '" + lastName + '\'' +
                ", email: '" + email + '\'' +
                ", age: " + age +
                ", phoneNumbers " + phoneNumbers +
                ", address " + address +
                ", jobTitle: '" + jobTitle + '\'' +
                ", company:  " + company +
                '}';
    }

    @Override
    public int compareTo(User o) {
        if (lastName.compareTo(o.lastName) == 0) {
            return firstName.compareTo(o.firstName);
        }
        return lastName.compareTo(o.lastName);
    }

    public void printUser() {
        if (getFirstName()!=null&getLastName()!=null) System.out.println(getFirstName() + " " + getLastName());
        if (getCompany()!=null) System.out.println(getCompany().getName());
        for (Map.Entry<String, PhoneNumber> phoneNumberEntry: getPhoneNumbers().entrySet()) {
            System.out.println(phoneNumberEntry.getKey());
            System.out.println(phoneNumberEntry.getValue());
        }
        if (getEmail()!=null)System.out.println(getEmail());
        System.out.println("home address");
        System.out.println(getAddress().streetName + " nr. " + getAddress().streetNumber + " floor " + getAddress().floor + "  ap. " + getAddress().apartmentNumber);
        System.out.println(getAddress().zipCode);
        System.out.println(getAddress().city);
        System.out.println(getAddress().country);
        System.out.println("work address");
        if (getCompany()!=null)System.out.println(getCompany().getAddress().streetName + " nr. " + getCompany().getAddress().streetNumber + " floor " + getCompany().getAddress().floor + "  ap. " + getCompany().getAddress().apartmentNumber);
        if (getCompany()!=null)System.out.println(getCompany().getAddress().zipCode);
        if (getCompany()!=null)System.out.println(getCompany().getAddress().city);
        if (getCompany()!=null)System.out.println(getCompany().getAddress().country);


    }
}
