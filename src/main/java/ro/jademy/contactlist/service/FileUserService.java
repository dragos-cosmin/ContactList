package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.User;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUserService implements UserService {

    private List<User> contacts=new ArrayList<>();

    public List<User> getContacts(){
        //check if contacts is empty
        if (contacts.isEmpty()){
            //read user properties from file
            //create users based on properties
            //add users to contacts

        }
        // else return the current list of contacts
        return contacts;
    }

    @Override
    public void addContact(User contact){
        //add user to Contact list
        contacts.add(contact);

        //write the whole list of contacts to file
        writeToFile(contacts);

    }

    @Override
    public Optional<User> search() {
        return Optional.empty();
    }

    public Optional<User> search(List<User> contacts, String query){


        return Optional.empty();
    }

    private void writeToFile(List<User> users){
        //TODO implement method
    }

    private void appendToFile (User contact){


    }

}
