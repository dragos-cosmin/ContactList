package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryUserService implements UserService {
    private List<User> contacts = new ArrayList<>();

    @Override
    public List<User> getContacts() {

        // check if contacts is empty and init the contact list only if this is true
        if (contacts.isEmpty()) {
            contacts.addAll(initContacts()); // get the contacts from the init method and add them to the contacts list, which should be used through the program
        }

        // else return the current list of contacts
        return contacts;
    }

    @Override
    public Optional<User> getContactById(int userId) {
        return contacts.stream().filter(u -> u.getId() == userId).findFirst();
    }


    @Override
    public void addContact(User contact) {
        // add user to contact list
        contacts.add(contact);
    }

    @Override
    public void editContact(int userId, String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, Address address, String jobTitle, Company company, boolean isFavorite) {
        Optional<User> userOpt = getContactById(userId);

        // edit the contact only if the user was found
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setAge(age);
            user.setPhoneNumbers(phoneNumbers);
            user.setAddress(address);
            user.setJobTitle(jobTitle);
            user.setCompany(company);
            user.setFavorite(isFavorite);

        }

    }

    @Override
    public void removeContact(int userId) {
        Optional<User> userOpt = getContactById(userId);

        // remove the contact only if found
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            contacts.remove(user);
        }
    }

    @Override
    public List<User> search(String query) {

        Supplier<Stream<User>> userlistStreamSupplier = () -> contacts.stream();

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

    @Override
    public void updateFromDataSource(){

    }

    @Override
    public void backupDataMenu(){

    }

    @Override
    public void backup(){

    }


    private List<User> initContacts() {
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

}
