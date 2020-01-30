package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;
import ro.jademy.contactlist.service.UserService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Menu {

    private UserService userService;


    public Menu(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public static void showMenu() {
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

    public void solveMenuTasks(int option) {
        long tInit, tFinal;
        Scanner scanner = new Scanner(System.in);
        switch (option) {

            case 1:
                //list contacts in natural order grouped by letter of first name
                tInit = System.nanoTime();
                Map<String, List<User>> userByFirstLetter = userService.getContacts()
                        .stream()
                        .sorted()
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
                    System.out.println("           " + (long) usr.size() + " contacts");
                    System.out.println();

                }
                tFinal = System.nanoTime();
                timeElapsed(tInit, tFinal);
                break;
            case 2:
                // display favorites list sorted
                tInit = System.nanoTime();
                System.out.println();
                System.out.println("****Favorite list****");
                System.out.println();
                userService.getContacts()
                        .stream()
                        .filter(user -> user.isFavorite())
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
                Optional<User> optionalUser = userService.getContactbyId(requestIndex);
                if (optionalUser.isPresent()) {
                    User requestUser = optionalUser.get();
                    requestUser.printUserDetails();
                } else {

                    System.out.println("The user has been previously removed, try another");
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
                List<User> searchedUser = userService.search(query);
                searchedUser.stream().sorted().forEach(user -> user.printUser());
                tFinal = System.nanoTime();
                timeElapsed(tInit, tFinal);
                break;
            case 5:
                // add new contact

                new User();
                User newUser;
                new User();
                User newUserNoComp;
                System.out.println("Input First Name: ");
                String firstName = scanner.next();
                System.out.println("Input Last Name: ");
                String lastName = scanner.next();
                System.out.println("Input age: ");
                Integer age = scanner.nextInt();
                System.out.println("Input email: ");
                String email = scanner.nextLine();
                Map<String, PhoneNumber> userPhones;
                userPhones = getPhonesFromKeyboard();
                System.out.println("Input home adress? Y/N");
                String choice = scanner.nextLine();
                Address homeAdress = new Address();
                if (choice.equalsIgnoreCase("Y")) {
                    homeAdress = createAdressFromKeyboard("home");
                }
                System.out.println("Input company details? Y/N");
                choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("Y")) {
                    System.out.println("Input company name: ");
                    String company = scanner.nextLine();
                    System.out.println("Input job title: ");

                    String jobTitle = scanner.nextLine();
                    Company userCompany = new Company();
                    try {
                        Address workAdress = createAdressFromKeyboard("work");
                        userCompany = new Company(company, workAdress);
                    } catch (InputMismatchException e) {
                        System.out.println("Incorect parameters" + e);
                    }

                    // scanner.nextLine();
                    System.out.println("Is favorite? true/false");

                    Boolean isFavorite = scanner.nextBoolean();
                    try {

                        newUser = new User(firstName, lastName, email, age, userPhones, homeAdress, jobTitle, userCompany, isFavorite);

                        Integer maxId = userService.getContacts().stream().mapToInt(user1 -> user1.getId()).max().getAsInt();
                        maxId++;
                        newUser.setId(maxId);
                    } catch (InputMismatchException e) {
                        System.out.println("There are some errors in completing the fields, try again");
                        break;
                    }

                    System.out.println("You added new user: \n");
                    newUser.printUserDetails();
                    System.out.println();
                    System.out.println("user id: " + newUser.getId());
                    userService.addContact(newUser);
                } else {
                    System.out.println("Is favorite? true/false");
                    Boolean isFavorite = scanner.nextBoolean();

                    try {
                        newUserNoComp = new User(firstName, lastName, email, age, userPhones, homeAdress, isFavorite);
                        Integer maxId = userService.getContacts().stream().mapToInt(user1 -> user1.getId()).max().getAsInt();
                        maxId++;
                        newUserNoComp.setId(maxId);
                    } catch (InputMismatchException e) {
                        System.out.println("There are some errors in completing the fields, try again");
                        break;
                    }
                    System.out.println("You added new user: ");
                    newUserNoComp.printUserDetails();
                    System.out.println();
                    System.out.println("user id: " + newUserNoComp.getId());
                    userService.addContact(newUserNoComp);

                }


                break;
            case 6:
                //edit contact
                System.out.println("input index: ");
                requestIndex = scanner.nextInt();
                tInit = System.nanoTime();
                Optional<User> optionalRequestUser = userService.getContactbyId(requestIndex);
                User requestUser = new User();
                if (optionalRequestUser.isPresent()) {
                    requestUser = optionalRequestUser.get();
                } else {

                    System.out.println("The user with this index has been previously removed, try another");

                }
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
                    System.out.println("EXIT - 0 ");
                    option = scanner.nextInt();
                    scanner.nextLine();
                    switch (option) {
                        case 1:
                            System.out.println("Input new first name: ");
                            firstName = scanner.nextLine();
                            requestUser.setFirstName(firstName);
                            break;
                        case 2:
                            System.out.println("Input new last name: ");
                            lastName = scanner.nextLine();
                            requestUser.setLastName(lastName);
                            break;
                        case 3:
                            System.out.println("Input new Company name: ");
                            String companyName = scanner.nextLine();
                            requestUser.getCompany().setName(companyName);
                            break;
                        case 4:
                            System.out.println("Input new job title: ");
                            String jobTitle = scanner.nextLine();
                            requestUser.setJobTitle(jobTitle);
                            break;
                        case 5:
                            requestUser.setPhoneNumbers(getPhonesFromKeyboard());
                            break;
                        case 6:
                            System.out.println("Input new email: ");
                            email = scanner.nextLine();
                            requestUser.setEmail(email);
                            break;
                        case 7:
                            System.out.println("Input adress type: (home/work)");
                            choice = scanner.nextLine();
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
                            System.out.println("isFavorite is: " + requestUser.isFavorite());
                            System.out.println("You want to change? Y/N");
                            if (scanner.nextLine().equalsIgnoreCase("Y")) {
                                System.out.println("Input new is Favorite: ");
                                requestUser.setFavorite(scanner.nextBoolean());
                            }
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Introduce valid option 1-8 or 0 to EXIT");
                            break;
                    }
                } while (option != 0);


                System.out.println();
                tFinal = System.nanoTime();
                System.out.println();
                timeElapsed(tInit, tFinal);

                break;


            case 7:
                //remove contact

                System.out.println("Input index: ");
                int index = scanner.nextInt();
                tInit = System.nanoTime();
                //Optional<User> optionalRemovedUser = userService.getContactbyId(index);
                //if (optionalRemovedUser.isPresent()) {
                //    userService.removeContact(index);
                //    System.out.print("Removed contact: ");
                //    optionalRemovedUser.get().printUser();
                //}
                //System.out.println("The user has been previously removed, try another");
                userService.removeContact(index);

                tFinal = System.nanoTime();
                timeElapsed(tInit, tFinal);

                break;

            case 8:
                //statistics
                // count contacts number
                tInit = System.nanoTime();
                final long count = userService.getContacts().stream().count();
                System.out.println("You have " + count + " contacts in total");

                final long favoriteCount = userService.getContacts().stream().filter(user -> user.isFavorite()).count();
                System.out.println("You have " + favoriteCount + " contacts in your favorite list");

                final OptionalInt minAge = userService.getContacts().stream().mapToInt(User::getAge).min();
                System.out.println("Minimum age is: " + minAge);

                tFinal = System.nanoTime();
                timeElapsed(tInit, tFinal);
                break;

            case 9:

                //Main.writeUserToFile("contactlist.csv", userService.getContacts(), FileUserService.HEADER, false);

                System.out.println("Good bye, see You soon!");
                System.exit(0);

                break;

            default:
                System.out.println("Please input option 1-9");

        }
    }

    public static Address createAdressFromKeyboard(String adresstype) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input " + adresstype + " adress:");
        System.out.println("Input street name: ");
        String streetName = scanner.nextLine();
        System.out.println("Input house number: ");
        Integer streetNumber = scanner.nextInt();
        System.out.println("Input apartment number: ");
        Integer apartmentNumber = scanner.nextInt();
        System.out.println("Input floor: ");
        String floor = scanner.next();
        System.out.println("Input Zip Code: ");
        String zipCode = scanner.next();
        System.out.println("Input cityname: ");
        String city = scanner.next();
        System.out.println("Input country: ");
        String country = scanner.next();
        return new Address(streetName, streetNumber, apartmentNumber, floor, zipCode, city, country);

    }

    public static Map<String, PhoneNumber> getPhonesFromKeyboard() {
        Scanner scanner = new Scanner(System.in);
        String choice;
        Map<String, PhoneNumber> userPhones = new HashMap<>();
        do {
            System.out.println("Input phone, choose work(w), home(h), mobile(m), exit(x): ");
            choice = scanner.next();

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

    public static void timeElapsed(long tInit, long tFinal) {
        long millis = TimeUnit.NANOSECONDS.toMillis(tFinal - tInit);
        System.out.println(String.format("operation complete in: %d ms", millis));

    }

}











