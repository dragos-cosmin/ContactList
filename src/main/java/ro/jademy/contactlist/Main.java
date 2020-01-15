package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // create a contact list of users
        // user 1

        Map<String, PhoneNumber>u1phoneNumbers=new HashMap<>();
        u1phoneNumbers.put("work",new PhoneNumber("+40","2204578"));
        u1phoneNumbers.put("home",new PhoneNumber("+40","7548924"));
        u1phoneNumbers.put("mobile",new PhoneNumber("+40","722125689"));

        Address u1HomeAddress=new Address("Stefan cel Mare",20,4,"parter","100066","Bucharest","Romania");
        Address u1CompAdress=new Address("Magheru",1,"12546","Bucharest","Romania");
        Company u1Company=new Company("IBM",u1CompAdress);

        User u1=new User("Dan","Popescu","danpopescu1@yahoo.com",45,u1phoneNumbers,u1HomeAddress,"technician",u1Company,false);
        //user 2
        Map<String, PhoneNumber>u2phoneNumbers=new HashMap<>();
        u2phoneNumbers.put("work",new PhoneNumber("+40","7945658"));
        u1phoneNumbers.put("home",new PhoneNumber("+40","2503056"));
        u1phoneNumbers.put("mobile",new PhoneNumber("+40","724359897"));

        Address u2HomeAddress=new Address("Dorobanti",10,8,"5","123456","Bucharest","Romania");
        Address u2CompAdress=new Address("Pipera",5,"560055","Bucharest","Romania");
        Company u2Company=new Company("Porche",u2CompAdress);

        User u2=new User("Andreea","Ionescu","aiones89@gmail.com",28,u2phoneNumbers,u2HomeAddress,"engineer",u2Company,false);

        // list contact list in natural order
        // list contact list by a given criteria
        // display a favorites list
        // search by a given or multiple criteria
        // display some statistics for the contact list
    }
}
