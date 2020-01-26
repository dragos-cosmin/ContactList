package ro.jademy.contactlist.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class PhoneNumber {
    Scanner scanner=new Scanner(System.in);
    private String countryCode; // ex: +40
    private String areaCode; // ex. 21
    private String number; // ex: 740123456

    public PhoneNumber(String countryCode, String areaCode, String number) {
        this.countryCode = countryCode;
        this.areaCode=areaCode;
        this.number = number;
    }

    public PhoneNumber(String areaCode, String number) {
        this.areaCode = areaCode;
        this.number = number;
    }

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
