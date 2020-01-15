package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        // create a contact list of users
        // user 1

        Map<String, PhoneNumber>u1phoneNumbers=new HashMap<>();
        u1phoneNumbers.put("work",new PhoneNumber("021","2204578"));
        u1phoneNumbers.put("home",new PhoneNumber("021","7548924"));
        u1phoneNumbers.put("mobile",new PhoneNumber("0722","125689"));

        Address u1HomeAddress=new Address("Stefan cel Mare",20,4,"parter","100066","Bucharest","Romania");
        Address u1CompAdress=new Address("Magheru",1,"12546","Bucharest","Romania");
        Company u1Company=new Company("IBM",u1CompAdress);

        User us1=new User("Andrei","Popescu","danpopescu1@yahoo.com",45,u1phoneNumbers,u1HomeAddress,"technician",u1Company,false);
        //user 2
        Map<String, PhoneNumber>u2phoneNumbers=new HashMap<>();
        u2phoneNumbers.put("work",new PhoneNumber("021","7945658"));
        u2phoneNumbers.put("home",new PhoneNumber("021","2503056"));
        u2phoneNumbers.put("mobile",new PhoneNumber("0724","359897"));

        Address u2HomeAddress=new Address("Dorobanti",10,8,"5","123456","Bucharest","Romania");
        Address u2CompAdress=new Address("Pipera",5,"560055","Bucharest","Romania");
        Company u2Company=new Company("Porche",u2CompAdress);

        User us2=new User("Andreea","Zaharescu","aiones89@gmail.com",28,u2phoneNumbers,u2HomeAddress,"engineer",u2Company,true);

        //user 3
        Map<String, PhoneNumber>u3phoneNumbers=new HashMap<>();
        u3phoneNumbers.put("work",new PhoneNumber("021","7945675"));
        u3phoneNumbers.put("home",new PhoneNumber("021","5528150"));
        u3phoneNumbers.put("mobile",new PhoneNumber("0734","125897"));

        Address u3HomeAddress=new Address("Drumul Taberei",125,23,"6","548796","Bucharest","Romania");
        Address u3CompAdress=new Address("Pipera",5,"560055","Bucharest","Romania");
        Company u3Company=new Company("Porche",u3CompAdress);

        User us3=new User("Dana","Cosma","thetin45@yahoo.com",35,u3phoneNumbers,u3HomeAddress,"accountant",u3Company,false);

        //user 4
        Map<String, PhoneNumber>u4phoneNumbers=new HashMap<>();

        u4phoneNumbers.put("home",new PhoneNumber("+40","21","2503056"));
        u1phoneNumbers.put("mobile",new PhoneNumber("0724","359897"));

        Address u4HomeAddress=new Address("Eugen Botez",24,2,"1","122589","Bucharest","Romania");

        User us4=new User("Ioana","Toma","tomna78@gmail.com",21,u4phoneNumbers,u4HomeAddress,true);

        List<User>contactList= Arrays.asList(us1,us2,us3,us4);



        // list contact list in natural order
        contactList
                .stream()
                .sorted()
                .forEach(System.out::println);

        System.out.println();

        // list contact list by a given criteria

        //============list contact list by First Name=========

        contactList
                .stream()
                .sorted((u1,u2)->u1.getFirstName().compareTo(u2.getFirstName()))
                .forEach(System.out::println);

        // display a favorites list

        System.out.println();
        System.out.println("Favorite list");
        System.out.println();

        contactList
                .stream()
                .filter(user -> user.isFavorite()==true)
                .sorted()
                .forEach(System.out::println);

        // search by a given or multiple criteria

        // search by First Name
        System.out.println();
        System.out.println("First name: ");
        String firstInputName=scanner.nextLine();
        contactList
                .stream()
                .filter(user ->user.getFirstName().equalsIgnoreCase(firstInputName) )
                .forEach(System.out::println);

        // search by Last Name
        System.out.println();
        System.out.println("Last name: ");

        String lastInputName=scanner.nextLine();

        contactList
                .stream()
                .filter(user ->user.getLastName().equalsIgnoreCase(lastInputName) )
                .forEach(System.out::println);

        //search by company
        System.out.println();
        System.out.println("Company: ");
        String companyInput=scanner.nextLine();
        contactList
                .stream()
                .filter(user -> user.getCompany()!=null)
                .filter(user ->user.getCompany().getName().equalsIgnoreCase(companyInput) )
                .forEach(System.out::println);
        // search on double criteria

        System.out.println();
        System.out.println("input company: ");
        String company2Input=scanner.nextLine();
        System.out.println("job title: ");
        String jobInput=scanner.nextLine();
        contactList
                .stream()
                .filter(user -> user.getCompany()!=null)
                .filter(user -> user.getCompany().getName().equalsIgnoreCase(company2Input)&& user.getJobTitle().equalsIgnoreCase(jobInput))
                .forEach(System.out::println);


        // display some statistics for the contact list

        // count contacts number

        final long count = contactList.stream().count();
        System.out.println("You have "+count+" contacts in total");

        final long favoriteCount=contactList.stream().filter(user -> user.isFavorite()==true).count();
        System.out.println("You have "+favoriteCount+" contacts in your favorite list");

        
    }
}
