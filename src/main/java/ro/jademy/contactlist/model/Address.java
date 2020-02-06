package ro.jademy.contactlist.model;

import java.util.Objects;

public class Address {

    String streetName;
    Integer streetNumber;
    Integer apartmentNumber;
    String floor;
    String zipCode;
    String city;
    String country;


    public Address(String streetName, Integer streetNumber, Integer apartmentNumber, String floor, String zipCode, String city, String country) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public Address(String streetName, Integer streetNumber, String zipCode, String city, String country) {
        this(streetName,streetNumber,null,null,null,city,country);
    }

    public Address(String streetName, Integer streetNumber, String city, String country){
        this(streetName,streetNumber,null,city,country);
    }

    public Address(){

    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Integer getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(Integer apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(streetName, address.streetName) &&
                Objects.equals(streetNumber, address.streetNumber) &&
                Objects.equals(apartmentNumber, address.apartmentNumber) &&
                Objects.equals(floor, address.floor) &&
                Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(city, address.city) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetName, streetNumber, apartmentNumber, floor, zipCode, city, country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetName: '" + streetName + '\'' +
                ", streetNumber: " + streetNumber +
                ", apartmentNumber: " + apartmentNumber +
                ", floor '" + floor + '\'' +
                ", zipCode: '" + zipCode + '\'' +
                ", city: '" + city + '\'' +
                ", country: '" + country + '\'' +
                '}';
    }

    public Address verifyAdress(Address address){
        Address resultAdress=new Address();
        if (address.getStreetName()==null){
            resultAdress.setStreetName("");
        } resultAdress.setStreetName(address.streetName);
        if (address.getStreetNumber()==null){
            resultAdress.setStreetNumber(0);
        } resultAdress.setStreetNumber(address.streetNumber);
        if (address.getApartmentNumber()==null){
            resultAdress.setApartmentNumber(0);
        } resultAdress.setApartmentNumber(address.apartmentNumber);



        return resultAdress;
    }




}
