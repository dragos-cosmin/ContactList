package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.File;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileUserService implements UserService {

    private File contactsFile;
    private List<User> contacts=new ArrayList<>();

    public FileUserService(File contactsFile) {
        this.contactsFile = contactsFile;
    }

    public FileUserService(String contactsFileName) {
        this(new File(contactsFileName));
    }

    public File getContactsFile() {
        return contactsFile;
    }

    public void setContactsFile(File contactsFile) {
        this.contactsFile = contactsFile;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    @Override
    public List<User> getContacts(){
        //check if contacts is empty
        if (contacts.isEmpty()){
            //read user properties from file
            //create users based on properties
            //add users to contacts
         contacts.addAll(readFromFile());

        }
        // else return the current list of contacts
        return contacts;
    }

    @Override
    public Optional<User> getContactbyId(int userId) {
        Optional<User> userOpt=contacts.stream().filter(user -> user.getId()==userId).findFirst();

        return userOpt;
    }

    @Override
    public void addContact(User contact){
        //add user to Contact list
        contacts.add(contact);

        //write the whole list of contacts to file
        writeToFile();

    }

    @Override
    public void editContact (int userId, String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, Address address, String jobTitle, Company company, boolean isFavorite) {

        Optional<User> userOpt = getContactbyId(userId);
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

            // overwrite the whole list of contacts in the file
            writeToFile();

        }
    }

    @Override
    public void removeContact(int userId) {

            Optional<User> userOpt = getContactbyId(userId);

            // remove the contact only if found
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                contacts.remove(user);
            }

            // TODO: write changes to file
    }

    @Override
    public List<User> search (String query) {
            // TODO: implement method

            return new ArrayList<>();
    }

    private List<User> readFromFile() {
        // TODO: read user properties from file and create the user list
        // TODO: remember to check if the file exists first (create it if it does not)

        return new ArrayList<>();
    }

    private void writeToFile(){
        //TODO implement method
    }

    private void appendToFile (User contact){


    }

}
