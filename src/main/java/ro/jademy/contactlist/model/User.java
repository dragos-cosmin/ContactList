package ro.jademy.contactlist.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class User implements Comparable<User> {

    private int id;
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

    public User(){}

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                isFavorite == user.isFavorite &&
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
        return Objects.hash(id, firstName, lastName, email, age, phoneNumbers, address, jobTitle, company, isFavorite);
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
        String mapToString="";
        String adressToString="";
        String userToString="";
        StringJoiner joinerPhone=new StringJoiner(",");
        StringJoiner joinerUserList=new StringJoiner("|");
        StringJoiner joinerHomeAddress=new StringJoiner("_");
        StringJoiner joinerWorkAddress=new StringJoiner("_");
        for (Map.Entry<String,PhoneNumber> mapEntry :phoneNumbers.entrySet() ) {
            mapToString=(mapEntry.getKey()+"_"+mapEntry.getValue().getCountryCode()+"_"+mapEntry.getValue().getAreaCode()+"_"+mapEntry.getValue().getNumber());
            joinerPhone.add(mapToString);
            mapToString=joinerPhone.toString();
        }
        if (company==null||company.getName().equalsIgnoreCase("")){

            adressToString="home_"+(joinerHomeAddress.add(address.streetName).add(Integer.toString(address.streetNumber)).add(Integer.toString(address.apartmentNumber)).add(address.floor).add(address.zipCode).add(address.city).add(address.country)).toString();
            userToString=(joinerUserList.add(Integer.toString(id)).add(firstName).add(lastName).add(mapToString).add(email).add(Integer.toString(age)).add(adressToString).add("").add("").add(Boolean.toString(isFavorite))).toString();

        } else {
            adressToString="home_"+(joinerHomeAddress.add(address.streetName).add(Integer.toString(address.streetNumber)).add(Integer.toString(address.apartmentNumber)).add(address.floor).add(address.zipCode).add(address.city).add(address.country)).toString()+",work_"+(joinerWorkAddress.add(company.getAddress().streetName).add(Integer.toString(company.getAddress().streetNumber)).add(Integer.toString(company.getAddress().apartmentNumber)).add(company.getAddress().floor).add(company.getAddress().zipCode).add(company.getAddress().city).add(company.getAddress().country)).toString();
            userToString=(joinerUserList.add(Integer.toString(id)).add(firstName).add(lastName).add(mapToString).add(email).add(Integer.toString(age)).add(adressToString).add(company.getName()).add(jobTitle).add(Boolean.toString(isFavorite))).toString();
        }



        return userToString;

    }

    @Override
    public int compareTo(User o) {
        if (lastName.compareTo(o.lastName) == 0) {
            return firstName.compareTo(o.firstName);
        }
        return lastName.compareTo(o.lastName);
    }

    public void printUser () {
        System.out.println(id + ". " + firstName + " " + lastName);


    }


    public void printUserDetails () {
        if (getFirstName() == null) {
            if (getLastName() == null) {
                System.out.println("    ");
            }
            System.out.println(getLastName());
        }
        System.out.println(getFirstName() + " " + getLastName());
        if (getCompany() != null&&(!getCompany().getName().equalsIgnoreCase(""))) System.out.println(getCompany().getName());
        if (jobTitle!=null&&(!getJobTitle().equalsIgnoreCase(""))) System.out.println(jobTitle);

        System.out.println();
        String phone = "";
        for (Map.Entry<String, PhoneNumber> phoneNumberEntry: getPhoneNumbers().entrySet()) {
            phone = phoneNumberEntry.getKey() + " " + phoneNumberEntry.getValue();
            System.out.printf("%17s %n", phone);
        }
        if (getEmail() != null) System.out.println(getEmail());
        System.out.println();
        if (getAddress() != null) {
            System.out.println("home address");
            System.out.println(getAddress().streetName + " nr. " + getAddress().streetNumber + " floor " + getAddress().floor + "  ap. " + getAddress().apartmentNumber);
            System.out.print(getAddress().zipCode);
            System.out.print(" " + getAddress().city);
            System.out.print(" " + getAddress().country);
        }
        System.out.println();
        System.out.println();
        if (getCompany() != null&&(!getCompany().getName().equalsIgnoreCase(""))) {
            System.out.println("work address");
            System.out.print(getCompany().getAddress().streetName + " nr. " + getCompany().getAddress().streetNumber);
            if (getCompany().getAddress().floor != null) System.out.print(" floor " + getCompany().getAddress().floor);
            if (getCompany().getAddress().apartmentNumber != null)
                System.out.println("  ap. " + getCompany().getAddress().apartmentNumber);
            System.out.print(getCompany().getAddress().zipCode);
            System.out.print(" " + getCompany().getAddress().city);
            System.out.print("  " + getCompany().getAddress().country);

        }


    }


}

