package ro.jademy.contactlist.model;

import java.util.Objects;
import java.util.Scanner;

public class PhoneNumber {
    Scanner scanner=new Scanner(System.in);
    private String countryCode; // ex: +40
    private String areaCode; // ex. 21
    private String number; // ex: 740123456

    public PhoneNumber(String countryCode, String areaCode, String number) {
        this.countryCode = countryCode; //ex: +40
        this.areaCode=areaCode; // ex: 244, 722
        this.number = number; //ex: 2105689 457856
    }

    public PhoneNumber(String areaCode, String number) {
        this.areaCode = areaCode;
        this.number = number;
    }

    public PhoneNumber(String number){
        this.countryCode="+40"; //implicit for Romania
        if (number.substring(1,3).equalsIgnoreCase("21")){
            this.areaCode="21";
            this.number=number.substring(3,number.length());
        }else {
            this.areaCode=number.substring(1,4);
            this.number=number.substring(4,number.length());
        }
    }

    public PhoneNumber(){}



    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }

    @Override
    public String toString() {
        String phoneToString="";
        if (countryCode==null){
            phoneToString=areaCode+number;
        } else {
            phoneToString=countryCode+areaCode+number;
        }

        return phoneToString;
    }



}
