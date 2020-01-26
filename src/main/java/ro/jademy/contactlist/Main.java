package ro.jademy.contactlist;


import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // create a contact list of users


        List<User> contactList = getUserListFromFile("contactlist.csv");
        String userHeader=readHeaderFromFile("contactlist.csv");

        // list contact list in natural order
        int opt;
        long tInit, tFinal;
        do {
            System.out.println();
            printMenu();
            System.out.println("Input option: ");
            opt = scanner.nextInt();

            switch (opt) {

                case 1:
                    tInit = System.nanoTime();
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
                    tFinal = System.nanoTime();
                    timeElapsed(tInit, tFinal);
                    break;
                case 2:
                    // display favorites list sorted
                    tInit = System.nanoTime();
                    System.out.println("****Favorite list****");
                    contactList
                            .stream()
                            .filter(user -> user.isFavorite() == true)
                            .sorted()
                            .forEach(user -> user.printUser());
                    tFinal = System.nanoTime();
                    timeElapsed(tInit, tFinal);

                    break;
                case 3:
                    //print details of user
                    System.out.println("input index: ");
                    Integer requestIndex = scanner.nextInt();
                    tInit = System.nanoTime();
                    try {

                        User requestUser = contactList.stream().filter(x -> requestIndex.equals(x.getId())).findAny().get();
                        requestUser.printUserDetails();
                    } catch (NoSuchElementException e) {
                        System.out.println("The user has been previously removed, try another");
                        break;
                    }
                    System.out.println();
                    tFinal = System.nanoTime();
                    System.out.println();
                    timeElapsed(tInit, tFinal);
                    break;
                case 4:
                    //search user

                    System.out.println("Input search query:");
                    String query = scanner.next();
                    tInit = System.nanoTime();
                    List<User> searchedUser = searchUser(contactList, query);
                    searchedUser.stream().sorted().forEach(user -> user.printUser());
                    tFinal = System.nanoTime();
                    timeElapsed(tInit, tFinal);
                    break;
                case 5:
                    // add new contact
                    User newUser = new User();
                    User newUserNoComp=new User();
                    System.out.println("Input First Name: ");
                    String firstName = scanner.next();
                    System.out.println("Input Last Name: ");
                    String lastName = scanner.next();
                    System.out.println("Input age: ");
                    Integer age = scanner.nextInt();
                    System.out.println("Input email: ");
                    String email = scanner.next();

                    String choice;
                    Map<String, PhoneNumber> userPhones = new HashMap<>();
                    userPhones=getPhonesFromKeyboard();
                    System.out.println("Input home adress? Y/N");
                    choice = scanner.next();
                    Address homeAdress = new Address();
                    if (choice.equalsIgnoreCase("Y")) {
                        homeAdress = Address.createAdressFromKeyboard("home");
                    }
                    System.out.println("Input company details? Y/N");
                    choice = scanner.next();

                    if (choice.equalsIgnoreCase("Y")) {
                        System.out.println("Input company name: ");
                        String company = scanner.next();
                        System.out.println("Input job title: ");
                        scanner.nextLine();
                        String jobTitle = scanner.next();
                        Company userCompany=new Company();
                        try {
                            Address workAdress = Address.createAdressFromKeyboard("work");
                            userCompany = new Company(company, workAdress);
                        }catch (InputMismatchException e){
                            System.out.println("Incorect parameters"+e);
                        }



                        System.out.println("Is favorite? true/false");

                        Boolean isFavorite = scanner.nextBoolean();
                        try {

                            newUser = createUser(firstName, lastName, email, age, userPhones, homeAdress, userCompany, jobTitle, isFavorite);

                            Integer maxId = contactList.stream().mapToInt(user1 -> user1.getId()).max().getAsInt();
                            maxId++;
                            newUser.setId(maxId);
                        } catch (InputMismatchException e) {
                            System.out.println("There are some errors in completing the fields, try again");
                            break;
                        }

                        System.out.println("You added new user: ");
                        newUser.printUserDetails();
                        System.out.println();
                        System.out.println("user id: " + newUser.getId());
                        contactList.add(newUser);
                    }else {
                        System.out.println("Is favorite? true/false");
                        Boolean isFavorite = scanner.nextBoolean();

                        try {
                            newUserNoComp=createUserNoCompany(firstName,lastName,email,age,userPhones,homeAdress,isFavorite);
                            Integer maxId = contactList.stream().mapToInt(user1 -> user1.getId()).max().getAsInt();
                            maxId++;
                            newUserNoComp.setId(maxId);
                        } catch (InputMismatchException e){
                            System.out.println("There are some errors in completing the fields, try again");
                            break;
                        }
                        System.out.println("You added new user: ");
                        newUserNoComp.printUserDetails();
                        System.out.println();
                        System.out.println("user id: " + newUserNoComp.getId());
                        contactList.add(newUserNoComp);



                    }



                    break;
                case 6:
                    //edit contact
                    System.out.println("input index: ");
                    requestIndex = scanner.nextInt();
                    tInit = System.nanoTime();
                    try {

                        User requestUser = contactList.stream().filter(x -> requestIndex.equals(x.getId())).findAny().get();

                        do {
                        requestUser.printUserDetails();
                            System.out.println();
                        System.out.println("Input field you want to modify: ");
                        System.out.println(" First name:      1");
                        System.out.println(" Last name:       2");
                        System.out.println(" Company name:    3");
                        System.out.println(" Job Title:       4");
                        System.out.println(" Phone numbers:   5");
                        System.out.println(" email:           6");
                        System.out.println(" Adresses:        7");
                        System.out.println(" isFavorite:      8");
                        //System.out.println("All fields - 9");
                        System.out.println("EXIT - 0 ");
                        opt=scanner.nextInt();
                        scanner.nextLine();
                        switch (opt){
                            case 1:
                                System.out.println("Input new first name: ");
                                firstName=scanner.nextLine();
                                requestUser.setFirstName(firstName);
                                break;
                            case 2:
                                System.out.println("Input new last name: ");
                                lastName=scanner.nextLine();
                                requestUser.setLastName(lastName);
                                break;
                            case 3:
                                System.out.println("Input new Company name: ");
                                String companyName=scanner.nextLine();
                                requestUser.getCompany().setName(companyName);
                                break;
                            case 4:
                                System.out.println("Input new job title: ");
                                String jobTitle=scanner.nextLine();
                                requestUser.setJobTitle(jobTitle);
                                break;
                            case 5:
                                requestUser.setPhoneNumbers(getPhonesFromKeyboard());
                                break;
                            case 6:
                                System.out.println("Input new email: ");
                                email=scanner.nextLine();
                                requestUser.setEmail(email);
                                break;
                            case 7:
                                System.out.println("Input adress type: (home/work)");
                                choice=scanner.nextLine();
                                switch (choice.toLowerCase()) {
                                    case ("home"):
                                        System.out.println("Street name is: " + requestUser.getAddress().getStreetName());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new street name: ");
                                            requestUser.getAddress().setStreetName(scanner.nextLine());
                                        }
                                        System.out.println("House number is: " + requestUser.getAddress().getStreetNumber());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new house number: ");
                                            requestUser.getAddress().setStreetNumber(scanner.nextInt());
                                            scanner.nextLine();
                                        }
                                        System.out.println("Apartment number is: " + requestUser.getAddress().getApartmentNumber());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new apartment number: ");
                                            requestUser.getAddress().setApartmentNumber(scanner.nextInt());
                                            scanner.nextLine();
                                        }
                                        System.out.println("Floor number is: " + requestUser.getAddress().getFloor());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new floor number: ");
                                            requestUser.getAddress().setFloor(scanner.nextLine());
                                        }
                                        System.out.println("ZipCode number is: " + requestUser.getAddress().getZipCode());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new zipCode: ");
                                            requestUser.getAddress().setZipCode(scanner.nextLine());
                                        }
                                        System.out.println("City is: " + requestUser.getAddress().getCity());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new city: ");
                                            requestUser.getAddress().setCity(scanner.nextLine());
                                        }
                                        System.out.println("Country is: " + requestUser.getAddress().getCountry());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new Country: ");
                                            requestUser.getAddress().setCountry(scanner.nextLine());
                                        }
                                        break;
                                    case ("work"):
                                        System.out.println("Street name is: " + requestUser.getCompany().getAddress().getStreetName());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new street name: ");
                                            requestUser.getCompany().getAddress().setStreetName(scanner.nextLine());
                                        }
                                        System.out.println("House number is: " + requestUser.getCompany().getAddress().getStreetNumber());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new house number: ");
                                            requestUser.getCompany().getAddress().setStreetNumber(scanner.nextInt());
                                            scanner.nextLine();
                                        }
                                        System.out.println("Apartment number is: " + requestUser.getCompany().getAddress().getApartmentNumber());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new apartment number: ");
                                            requestUser.getCompany().getAddress().setApartmentNumber(scanner.nextInt());
                                            scanner.nextLine();
                                        }
                                        System.out.println("Floor number is: " + requestUser.getCompany().getAddress().getFloor());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new floor number: ");
                                            requestUser.getCompany().getAddress().setFloor(scanner.nextLine());
                                        }
                                        System.out.println("ZipCode number is: " + requestUser.getCompany().getAddress().getZipCode());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new zipCode: ");
                                            requestUser.getCompany().getAddress().setZipCode(scanner.nextLine());
                                        }
                                        System.out.println("City is: " + requestUser.getCompany().getAddress().getCity());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new city: ");
                                            requestUser.getCompany().getAddress().setCity(scanner.nextLine());
                                        }
                                        System.out.println("Country is: " + requestUser.getCompany().getAddress().getCountry());
                                        System.out.println("You want to change? (Y/N)");
                                        if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                            System.out.println("Input new Country: ");
                                            requestUser.getCompany().getAddress().setCountry(scanner.nextLine());
                                        }
                                        break;

                                    default:
                                        System.out.println("Options home/work");
                                        break;
                                }
                                break;
                            case 8:
                                System.out.println("isFavorite is: "+requestUser.isFavorite());
                                System.out.println("You want to change? Y/N");
                                if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                    System.out.println("Input new is Favorite: ");
                                    requestUser.setFavorite(scanner.nextBoolean());
                                }
                                break;
                            case 9:
                                break;
                            case 0:
                                break;
                        }
                        } while (opt!=0);

                    } catch (NoSuchElementException e) {
                        System.out.println("The user has been previously removed, try another");
                        break;
                    }
                    System.out.println();
                    tFinal = System.nanoTime();
                    System.out.println();
                    timeElapsed(tInit, tFinal);
                    break;


                case 7:
                    //remove contact

                    System.out.println("Input index: ");
                    Integer index = scanner.nextInt();
                    tInit = System.nanoTime();
                    try {
                        User removedUser = contactList.stream().filter(user -> user.getId() == index).findFirst().get();
                        contactList.remove(removedUser);
                        System.out.print("Removed contact: ");
                        removedUser.printUser();
                    } catch (NoSuchElementException e) {
                        System.out.println("The user has been previously removed, try another");
                        break;
                    }

                    tFinal = System.nanoTime();
                    timeElapsed(tInit, tFinal);

                    break;

                case 8:
                    //statistics
                    // count contacts number
                    tInit = System.nanoTime();
                    final long count = contactList.stream().count();
                    System.out.println("You have " + count + " contacts in total");

                    final long favoriteCount = contactList.stream().filter(user -> user.isFavorite()).count();
                    System.out.println("You have " + favoriteCount + " contacts in your favorite list");

                    final OptionalInt minAge = contactList.stream().mapToInt(User::getAge).min();
                    System.out.println("Minimum age is: " + minAge);

                    tFinal = System.nanoTime();
                    timeElapsed(tInit, tFinal);
                    break;

                case 9:

                    writeUserToFile("contactlist.csv",contactList,userHeader,false);
                    System.out.println("Good bye, see You soon!");
                    System.exit(0);

                    break;

                default:
                    System.out.println("Please input option 1-9");

            }

        } while (opt != 9);

    }

    // Methods:


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

        List<User> contactList = new ArrayList<>();
        contactList.add(us1);
        contactList.add(us2);
        contactList.add(us3);
        contactList.add(us4);
        contactList.add(us5);
        contactList.add(us6);
        return contactList;

    }

    public static List<User> getUserListFromFile(String userFileName) {
        List<User> contactList = new ArrayList<>();
        List<String> fileLines = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(userFileName)));
            fileLines = in.lines().collect(Collectors.toList());


        } catch (IOException ex) {
            System.out.println("File not found\n" + ex);
        }

        for (String s: fileLines) {
            if (!s.contains("INDEX")) {


                String[] fields = s.split("\\|");
                Integer userId = Integer.parseInt(fields[0]);
                String firstName = fields[1];
                String lastName = fields[2];
                String phoneNumbers = fields[3];
                String[] phones = phoneNumbers.split(",");
                Map<String, PhoneNumber> phoneNumber = new HashMap<>();
                for (int i = 0; i < phones.length; i++) {
                    String[] phoneFields = phones[i].split("_");
                    String phoneType = phoneFields[0];
                    String phoneCountryCode = phoneFields[1];
                    String phoneAreaCode = phoneFields[2];
                    String phoneNo = phoneFields[3];
                    phoneNumber.put(phoneType, new PhoneNumber(phoneCountryCode, phoneAreaCode, phoneNo));
                }

                String email = fields[4];
                Integer age = Integer.parseInt(fields[5]);
                String adresses = fields[6];
                Address homeAdress = new Address();
                Address workAdress = new Address();
                String[] adress = adresses.split(",");
                for (int i = 0; i < adress.length; i++) {
                    String[] adressFields = adress[i].split("_");
                    if (adressFields[0].equalsIgnoreCase("home")) {
                        homeAdress.setStreetName(adressFields[1]);
                        homeAdress.setStreetNumber(Integer.parseInt(adressFields[2]));
                        homeAdress.setApartmentNumber(Integer.parseInt(adressFields[3]));
                        homeAdress.setFloor(adressFields[4]);
                        homeAdress.setZipCode(adressFields[5]);
                        homeAdress.setCity(adressFields[6]);
                        homeAdress.setCountry(adressFields[7]);

                    } else if (adressFields[0].equalsIgnoreCase("work")) {
                        workAdress.setStreetName(adressFields[1]);
                        workAdress.setStreetNumber(Integer.parseInt(adressFields[2]));
                        workAdress.setApartmentNumber(Integer.parseInt(adressFields[3]));
                        workAdress.setFloor(adressFields[4]);
                        workAdress.setZipCode(adressFields[5]);
                        workAdress.setCity(adressFields[6]);
                        workAdress.setCountry(adressFields[7]);

                    }
                }


                String companyName = fields[7];
                Company userCompany = new Company(companyName, workAdress);
                String jobTitle = fields[8];
                Boolean isFavorite = Boolean.parseBoolean(fields[9]);
                User user = createUser(firstName, lastName, email, age, phoneNumber, homeAdress, userCompany, jobTitle, isFavorite);
                user.setId(userId);
                contactList.add(user);
            }

        }

        return contactList;
    }
        public static String readHeaderFromFile (String fileName){

        String header="";
        try (BufferedReader in=new BufferedReader(new FileReader(fileName))){

                header=in.readLine().toString();


            } catch (IOException ex){
                System.out.println("File not found\n" + ex);
            }


        return header;

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
        Supplier<Stream<User>> userlistStreamSupplier = () -> userList.stream();

        List<User> resultFirsName = userlistStreamSupplier.get()
                .filter(user -> user.getFirstName() != null)
                .filter(user -> user.getFirstName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultLastName = userlistStreamSupplier.get()
                .filter(user -> user.getLastName() != null)
                .filter(user -> user.getLastName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultCompany = userlistStreamSupplier.get()
                .filter(user -> user.getCompany() != null)
                .filter(user -> user.getCompany().getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultJobTitle = userlistStreamSupplier.get()
                .filter(user -> user.getJobTitle() != null)
                .filter(user -> user.getJobTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<User> resultPhone = userlistStreamSupplier.get()
                .filter(user -> user.getPhoneNumbers() != null)
                .filter(user -> user.getPhoneNumbers().values()
                        .stream()
                        .filter(phoneNumber -> phoneNumber.getNumber() != null)
                        .map(phoneNumber -> phoneNumber.getCountryCode() + phoneNumber.getAreaCode() + phoneNumber.getNumber())
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

    public static User createUser(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, Address homeAdress, Company company, String jobTitle, boolean isfavorite) throws InputMismatchException {

        return new User(firstName, lastName, email, age, phoneNumbers, homeAdress, jobTitle, company, isfavorite);
    }

    public static User createUserNoCompany (String firstName, String lastName, String email, Integer age, Map<String,PhoneNumber>phoneNumbers,Address homeAdress,boolean isFavorite){
        return new User(firstName,lastName,email,age,phoneNumbers,homeAdress,isFavorite);
    }

    public static void writeUserToFile(String fileName, List<User> userList, String header, boolean append) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName, append))) {

            out.append(header);
            for (User u: userList) {
                out.newLine();
                out.append(u.toString());
            }


        } catch (IOException ex) {

            System.out.println("File not found\n" + ex);
        }


    }


    public static void timeElapsed(long tInit, long tFinal) {
        long millis = TimeUnit.NANOSECONDS.toMillis(tFinal - tInit);
        System.out.println(String.format("operation complete in: %d ms", millis));

    }

    public static Map<String,PhoneNumber> getPhonesFromKeyboard (){
        Scanner scanner=new Scanner(System.in);
        String choice;
        Map<String, PhoneNumber> userPhones = new HashMap<>();
        do {
            System.out.println("Input phone, choose work(w), home(h), mobile(m), exit(x): ");
            choice =scanner.next();

            switch (choice.toLowerCase()) {
                case "w":
                    System.out.println("input workPhone CountryCode: ");
                    String countryCode = scanner.next();
                    System.out.println("input workPhone AreaCode: ");
                    String areaCode = scanner.next();
                    System.out.println("input workPhone number: ");
                    String workNumber = scanner.next();
                    userPhones.put("work", new PhoneNumber(countryCode, areaCode, workNumber));
                    break;
                case "h":
                    System.out.println("input homePhone CountryCode: ");
                    countryCode = scanner.next();
                    System.out.println("input homePhone AreaCode: ");
                    areaCode = scanner.next();
                    System.out.println("input homePhone number: ");
                    String homeNumber = scanner.next();
                    userPhones.put("home", new PhoneNumber(countryCode, areaCode, homeNumber));
                    break;
                case "m":
                    System.out.println("input mobilePhone CountryCode: ");
                    countryCode = scanner.next();
                    System.out.println("input mobilePhone AreaCode: ");
                    areaCode = scanner.next();
                    System.out.println("input mobilePhone number: ");
                    String mobileNumber = scanner.next();
                    userPhones.put("mobile", new PhoneNumber(countryCode, areaCode, mobileNumber));
                    break;
                case ("x"):
                    break;

                default:
                    System.out.println("input valid options: w/h/m/x");
                    break;
            }
        } while (!choice.equalsIgnoreCase("x"));
        return userPhones;
    }

}
