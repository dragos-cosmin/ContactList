package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // create a contact list of users

        List<User> contactList = getUserList();

        // list contact list in natural order
        int opt;
        do {
            System.out.println();
            printMenu();
            System.out.println("Input option: ");
            opt = scanner.nextInt();

            switch (opt) {

                case 1:

                    Map<String, List<User>> userByFirstLetter = contactList
                            .stream()
                            .collect(Collectors.groupingBy(user -> {
                                        if (Character.isLetter(user.getFirstName().substring(0, 1).charAt(0)))
                                            return user.getFirstName().substring(0, 1);

                                        return "#";
                                    }
                                    , TreeMap::new, Collectors.toList()));


                    for (Map.Entry<String, List<User>> listEntry: userByFirstLetter.entrySet()) {
                        System.out.println(listEntry.getKey());
                        System.out.println("--------------------");
                        List<User> usr = listEntry.getValue();

                        for (User u: usr)
                            System.out.println(u.getId() + ". " + u.getFirstName() + " " + u.getLastName());
                        System.out.println("--------------------");
                        System.out.println("           " + usr.stream().count() + " contacts");
                        System.out.println();

                    }

                    break;
                case 2:
                    // display favorites list sorted
                    System.out.println("****Favorite list****");
                    contactList
                            .stream()
                            .filter(user -> user.isFavorite() == true)
                            .sorted()
                            .forEach(System.out::println);
                    break;
                case 3:
                    System.out.println("input index: ");
                    Integer requestIndex = scanner.nextInt();
                    User requestUser = contactList.stream().filter(x -> requestIndex.equals(x.getId())).findAny().get();
                    requestUser.printUser();
                    System.out.println();
                    break;
                case 4:
                    //search user

                    System.out.println("Input search query:");
                    String query = scanner.next();
                    List<User> searchedUser = searchUser(contactList, query);
                    searchedUser.stream().sorted().forEach(System.out::println);

                    break;
            }

        } while (opt != 9);
        // list contact list by a given criteria

        //============list contact list by First Name=========

        contactList
                .stream()
                .sorted((u1, u2) -> u1.getFirstName().compareTo(u2.getFirstName()))
                .forEach(System.out::println);
        //list contact list sorting by age and then by last name

        contactList
                .stream()
                .sorted(Comparator.comparing(User::getAge).thenComparing(User::getLastName))
                .forEach(System.out::println);


        // display some statistics for the contact list

        // count contacts number

        final long count = contactList.stream().count();
        System.out.println("You have " + count + " contacts in total");

        final long favoriteCount = contactList.stream().filter(user -> user.isFavorite()).count();
        System.out.println("You have " + favoriteCount + " contacts in your favorite list");

        final OptionalInt minAge = contactList.stream().mapToInt(User::getAge).min();
        System.out.println("Minimum age is: " + minAge);

        // list grouping by letter in FirstName and LastName
        long t0 = System.nanoTime();


        long t1 = System.nanoTime();
        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("execution of sort by letters took: %d ms", millis));


    }


    public static List<User> getUserList() {
        // user 1
        Map<String, PhoneNumber> u1phoneNumbers = new HashMap<>();
        u1phoneNumbers.put("work", new PhoneNumber("021", "2204578"));
        u1phoneNumbers.put("home", new PhoneNumber("021", "7548924"));
        u1phoneNumbers.put("mobile", new PhoneNumber("0722", "125689"));

        Address u1HomeAddress = new Address("Stefan cel Mare", 20, 4, "parter", "100066", "Bucharest", "Romania");
        Address u1CompAdress = new Address("Magheru", 1, "12546", "Bucharest", "Romania");
        Company u1Company = new Company("IBM", u1CompAdress);

        User us1 = new User("Andrei", "Popescu", "danpopescu1@yahoo.com", 45, u1phoneNumbers, u1HomeAddress, "technician", u1Company, false);
        us1.setId(1);

        //user 2
        Map<String, PhoneNumber> u2phoneNumbers = new HashMap<>();
        u2phoneNumbers.put("work", new PhoneNumber("021", "7945658"));
        u2phoneNumbers.put("home", new PhoneNumber("021", "2503056"));
        u2phoneNumbers.put("mobile", new PhoneNumber("0724", "359897"));

        Address u2HomeAddress = new Address("Dorobanti", 10, 8, "5", "123456", "Bucharest", "Romania");
        Address u2CompAdress = new Address("Pipera", 5, "560055", "Bucharest", "Romania");
        Company u2Company = new Company("Porche", u2CompAdress);

        User us2 = new User("Andreea", "Zaharescu", "aiones89@gmail.com", 28, u2phoneNumbers, u2HomeAddress, "engineer", u2Company, false);
        us2.setId(2);

        //user 3
        Map<String, PhoneNumber> u3phoneNumbers = new HashMap<>();
        u3phoneNumbers.put("work", new PhoneNumber("021", "7945675"));
        u3phoneNumbers.put("home", new PhoneNumber("021", "5528150"));
        u3phoneNumbers.put("mobile", new PhoneNumber("0734", "125897"));

        Address u3HomeAddress = new Address("Drumul Taberei", 125, 23, "6", "548796", "Bucharest", "Romania");
        Address u3CompAdress = new Address("Pipera", 5, "560055", "Bucharest", "Romania");
        Company u3Company = new Company("Porche", u3CompAdress);

        User us3 = new User("Dana", "", "thetin45@yahoo.com", 35, u3phoneNumbers, u3HomeAddress, "accountant", u3Company, false);
        us3.setId(3);

        //user 4
        Map<String, PhoneNumber> u4phoneNumbers = new HashMap<>();

        u4phoneNumbers.put("home", new PhoneNumber("+40", "21", "2503056"));
        u4phoneNumbers.put("mobile", new PhoneNumber("0724", "359897"));

        Address u4HomeAddress = new Address("Eugen Botez", 24, 2, "1", "122589", "Bucharest", "Romania");

        User us4 = new User("Ioana", "Toma", "tomna78@gmail.com", 21, u4phoneNumbers, u4HomeAddress, true);
        us4.setId(4);

        //user 5
        Map<String, PhoneNumber> u5phoneNumbers = new HashMap<>();

        u5phoneNumbers.put("home", new PhoneNumber("+40", "21", "2503056"));
        u5phoneNumbers.put("mobile", new PhoneNumber("0724", "359897"));

        Address u5HomeAddress = new Address("Eugen Botez", 24, 2, "1", "122589", "Bucharest", "Romania");

        User us5 = new User("?", "!", "tomna78@gmail.com", 21, u4phoneNumbers, u4HomeAddress, false);
        us5.setId(5);

        //user 6
        Map<String, PhoneNumber> u6phoneNumbers = new HashMap<>();

        u6phoneNumbers.put("home", new PhoneNumber("+40", "21", "2503056"));
        u6phoneNumbers.put("mobile", new PhoneNumber("0724", "359897"));

        Address u6HomeAddress = new Address("Eugen Botez", 24, 2, "1", "122589", "Bucharest", "Romania");

        User us6 = new User("@", "Coco", "tomna78@gmail.com", 21, u4phoneNumbers, u4HomeAddress, true);
        us6.setId(6);

        List<User> contactList = Arrays.asList(us1, us2, us3, us4, us5, us6);
        return contactList;

    }

    public static void printMenu() {
        System.out.println("   CONTACT LIST    ");
        System.out.println("===================");
        System.out.println("1. List contacts   ");
        System.out.println("2. List favorites  ");
        System.out.println("3. Details by id   ");
        System.out.println("4. Search contact  ");
        System.out.println("5. Add new contact ");
        System.out.println("6. Edit contact    ");
        System.out.println("7. Remove contact  ");
        System.out.println("8. Statistics      ");
        System.out.println("9. EXIT            ");
        System.out.println("===================");

    }

    public static List<User> searchUser(List<User> userList, String query) {
        List<User> resultFirsName = userList
                .stream()
                .filter(user -> user.getFirstName() != null)
                .filter(user -> user.getFirstName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultLastName = userList
                .stream()
                .filter(user -> user.getLastName() != null)
                .filter(user -> user.getLastName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultCompany = userList
                .stream()
                .filter(user -> user.getCompany() != null)
                .filter(user -> user.getCompany().getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultJobTitle = userList
                .stream()
                .filter(user -> user.getJobTitle() != null)
                .filter(user -> user.getJobTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultPhone = userList
                .stream()
                .filter(user -> user.getPhoneNumbers() != null)
                .filter(user -> user.getPhoneNumbers().values()
                        .stream()
                        .filter(phoneNumber -> phoneNumber.getNumber() != null)
                        .map(phoneNumber -> phoneNumber.getCountryCode()+phoneNumber.getAreaCode()+phoneNumber.getNumber())
                        .anyMatch(phoneNumber -> phoneNumber.contains(query)))
                .collect(Collectors.toList());
        List<User> resultList = new ArrayList<>();
        resultList.addAll(resultFirsName);
        resultList.addAll(resultLastName);
        resultList.addAll(resultCompany);
        resultList.addAll(resultJobTitle);
        resultList.addAll(resultPhone);
        return resultList;

    }


}
