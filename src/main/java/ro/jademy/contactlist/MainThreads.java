package ro.jademy.contactlist;

import ro.jademy.contactlist.service.FileUserService;
import ro.jademy.contactlist.service.UserService;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainThreads {
    public static long lastModified=0;
    public static void main(String[] args) {

        Scanner scanner=new Scanner(System.in);
        ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
        int option;
        UserService userService = new FileUserService("contactlist.csv");
        userService.getContacts();
        Menu menu = new Menu(userService);
        lastModified=((FileUserService) userService).getContactsFile().lastModified();

        executorService.scheduleWithFixedDelay(()->{  // pt thread at fixed delay

            if (!(((FileUserService) userService).getContactsFile().lastModified() ==lastModified)){
                ((FileUserService) userService).getContacts().clear();
                userService.getContacts();
                lastModified=((FileUserService) userService).getContactsFile().lastModified();
            }

            //System.out.println("Nothing changed");




        },1,10, TimeUnit.SECONDS);

        do {
            System.out.println();
            Menu.showMenu();
            System.out.println("Input option: ");
            option = scanner.nextInt();
            menu.solveMenuTasks(option);

        } while (option != 0);

    }

}



