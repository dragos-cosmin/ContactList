package ro.jademy.contactlist.model;

import java.util.Objects;

public class Company {

    private String name;
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Company(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public Company(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name) &&
                Objects.equals(address, company.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
