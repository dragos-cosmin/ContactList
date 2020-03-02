package ro.jademy.contactlist;

import ro.jademy.contactlist.service.DataBaseUserService;
import ro.jademy.contactlist.service.FileUserService;
import ro.jademy.contactlist.service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;
        //create contactlist of users
        //list contact list in natural order
        //list contact list by given criteria
        //display favorite list
        //search by a given or multiple criteria
        // display some statistics for the contact list


        // create a contact list of users
        //UserService userService = new FileUserService("contactlist.csv"); // User service - File user service
        //   UserService userService = new MemoryUserService(); // User service - Memory user service
  //      UserService userService=new DataBaseUserService("contactlist","127.0.0.1","3306","root","Px707244@PH925477");
        UserService userService=new DataBaseUserService("dbproperties.prop");
        userService.getContacts();
        Menu menu = new Menu(userService);

        do {
            System.out.println();
            Menu.showMenu();
            System.out.println("Input option: ");
            option = scanner.nextInt();
            menu.solveMenuTasks(option);

        } while (option != 0);

    }

}
