package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getContacts();

    void addContact(User user);

    Optional<User> search();





}
