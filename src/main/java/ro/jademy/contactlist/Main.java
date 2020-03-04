package ro.jademy.contactlist;

import ro.jademy.contactlist.service.DataBaseUserService;
import ro.jademy.contactlist.service.FileUserService;
import ro.jademy.contactlist.service.MemoryUserService;
import ro.jademy.contactlist.service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;
        UserService userService = new MemoryUserService(); // User service - Memory user service
        System.out.println("Default MemoryUserService, if you want to change input (f) FileUserService, (d) DataBaseUserService :");
        String servOpt = scanner.nextLine().toLowerCase();
        switch (servOpt) {
            case ("f"):
                userService = new FileUserService("contactlist.csv"); // User service - File user service
                break;
            case ("d"):
                userService = new DataBaseUserService("dbproperties.prop");
                break;
            default:
                System.out.println("input f or d if you want to change. MemoryUserService default");
        }

        userService.getContacts();
        Menu menu = new Menu(userService);
        userService.updateFromDataSource();

        do {
            System.out.println();
            Menu.showMenu();
            System.out.println("Input option: ");
            option = scanner.nextInt();
            menu.solveMenuTasks(option);

        } while (option != 0);

    }

}
