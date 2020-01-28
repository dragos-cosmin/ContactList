package ro.jademy.contactlist;

import ro.jademy.contactlist.service.UserService;

import java.util.Scanner;

public class Menu {
    private Scanner scanner=new Scanner(System.in);
    private UserService userService;

    public Menu(UserService userService) {
        this.userService = userService;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
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




}
