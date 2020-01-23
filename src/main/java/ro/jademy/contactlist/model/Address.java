package ro.jademy.contactlist.model;

import java.util.Objects;
import java.util.Scanner;

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
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public Address(){

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

    public static Address createAdressFromKeyboard (String adresstype){
        Scanner scanner=new Scanner(System.in);
        System.out.println("Input "+adresstype+" adress:");
        System.out.println("Input street name: ");
        String streetName=scanner.nextLine();
        System.out.println("Input house number: ");
        Integer streetNumber=scanner.nextInt();
        System.out.println("Input apartment number: ");
        Integer apartmentNumber=scanner.nextInt();
        System.out.println("Input floor: ");
        String floor=scanner.next();
        System.out.println("Input Zip Code: ");
        String zipCode=scanner.next();
        System.out.println("Input cityname: ");
        String city=scanner.next();
        System.out.println("Input country: ");
        String country=scanner.next();
        return new Address(streetName,streetNumber,apartmentNumber,floor,zipCode,city,country);



    }



}
