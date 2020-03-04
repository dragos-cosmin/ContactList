package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUserService implements UserService {

    private File contactsFile;
    private List<User> contacts = new ArrayList<>();

    public static final String HEADER = "INDEX|F_NAME|L_NAME|PHONE_NUMBERS|EMAIL|AGE|ADRESSES|COMPANY_NAME|JOB_TITLE|IS_FAVORITE";

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
    public List<User> getContacts() {
        //check if contacts is empty
        if (contacts.isEmpty()) {
            //read user properties from file
            //create users based on properties
            //add users to contacts
            contacts.addAll(readFromFile());
            backupFile();

        }
        // else return the current list of contacts

        return contacts;

    }

    @Override
    public Optional<User> getContactById(int userId) {

        return contacts.stream().filter(user -> user.getId() == userId).findFirst();
    }

    @Override
    public void addContact(User contact) {
        //add user to Contact list
        contacts.add(contact);

        //write the whole list of contacts to file
        appendToFile(contact);

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

            // overwrite the whole list of contacts in the file
            writeToFile();

        } else System.out.println("User does not exist, try another");
    }

    @Override
    public void removeContact(int userId) {

        Optional<User> userOpt = getContactById(userId);

        // remove the contact only if found
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            contacts.remove(user);
            System.out.println("User removed:");
            user.printUserDetails();
            writeToFile();
        } else {
            System.out.println("User has been previously removed, try another");
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
    public void backup(){
        backupFile();
    }


    private List<User> readFromFile() {
        if (!contactsFile.exists()) {
            try {
                contactsFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Cannot create file" + e);
            }

        }
        List<User> contactList = new ArrayList<>();
        List<String> fileLines = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(contactsFile))) {
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
                String ageStr= fields[5];
                Integer age;
                if (ageStr.equalsIgnoreCase("")){
                    age=null;
                }else {
                    age = Integer.parseInt(ageStr);
                }

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
                        if (adressFields[1].equalsIgnoreCase("")){
                            workAdress.setStreetName(null);
                        } else workAdress.setStreetName(adressFields[1]);
                        if (adressFields[2].equalsIgnoreCase("")){
                            workAdress.setStreetNumber(null);
                        }else workAdress.setStreetNumber(Integer.parseInt(adressFields[2]));
                        if (adressFields[3].equalsIgnoreCase("")){
                            workAdress.setApartmentNumber(null);
                        } else workAdress.setApartmentNumber(Integer.parseInt(adressFields[3]));
                        if (adressFields[4].equalsIgnoreCase("")){
                            workAdress.setFloor(null);
                        } else workAdress.setFloor(adressFields[4]);
                        if (adressFields[5].equalsIgnoreCase("")){
                            workAdress.setZipCode(null);
                        } else workAdress.setZipCode(adressFields[5]);
                        workAdress.setCity(adressFields[6]);
                        workAdress.setCountry(adressFields[7]);

                    }
                }


                String companyName = fields[7];
                Company userCompany = new Company(companyName, workAdress);
                String jobTitle = fields[8];
                boolean isFavorite = Boolean.parseBoolean(fields[9]);
                User user = new User(firstName, lastName, email, age, phoneNumber, homeAdress, jobTitle, userCompany, isFavorite);
                user.setId(userId);
                contactList.add(user);
            }

        }

        return contactList;

    }

    private void writeToFile() {

        try (BufferedWriter out = new BufferedWriter(new FileWriter(contactsFile, false))) {
            out.append(HEADER);
            for (User u: contacts) {
                out.newLine();
                out.append(u.toString());
            }

        } catch (IOException ex) {
            System.out.println("File not found\n" + ex);
        }

    }

    private void appendToFile(User contact) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(contactsFile, true))) {
            out.newLine();
            out.append(contact.toString());

        } catch (IOException ex) {
            System.out.println("File not found\n" + ex);
        }
    }

    public void backupFile() {

        if (contactsFile.exists()) {
            String unicId = UUID.randomUUID().toString();

            File backupFile = new File("contactfile_backup" + unicId + ".csv");

            try {
                backupFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Cannot create backup file");
            }
            List<String> fileLines = new ArrayList<>();
            try (BufferedReader in = new BufferedReader(new FileReader(contactsFile))) {
                fileLines = in.lines().collect(Collectors.toList());
            } catch (IOException ex) {
                System.out.println("Cannot read from file\n" + ex);
            }
            try (BufferedWriter out = new BufferedWriter(new FileWriter(backupFile, false))) {
                for (String s: fileLines) {
                    out.append(s);
                    out.newLine();
                }
            } catch (IOException e) {
                System.out.println("Cannot write to file\n" + e);
            }

        }
    }

    public void restoreFromBackupFile(String backupFileName) {

        if (!contactsFile.exists()) {
            try {
                contactsFile.createNewFile();
            } catch (IOException e) {
                System.out.println(e.fillInStackTrace());
            }

        }
        List<String> fileLines = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(backupFileName))) {
            fileLines = in.lines().collect(Collectors.toList());
        } catch (IOException ex) {
            System.out.println("Cannot read from file\n" + ex);
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(contactsFile, false))) {
            for (String s: fileLines) {
                out.append(s);
                out.newLine();
            }
        } catch (IOException e) {
            System.out.println("Cannot write to file\n" + e);
        }
        contacts.clear();
        getContacts();
    }

    public static List<File> getFilesFromDir(String fullAbsolutePathName, String containingName, String extension) {
        List<File> resultFiles = new ArrayList<>();
        try (Stream<Path> fileStream = Files.walk(Paths.get(fullAbsolutePathName))) {
            resultFiles = fileStream
                    .filter(f -> f.getFileName().toString().contains(containingName))
                    .filter(f->f.getFileName().toString().contains(extension))
                    .map(Path::toFile)
                    .collect(Collectors.toMap(Function.identity(), File::lastModified))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        } catch (IOException ex) {
            System.out.println(ex.fillInStackTrace());
        }


        return resultFiles;
    }

    public void printFileNames(String containtingName) {
        String absolutePathName = contactsFile.getAbsolutePath().substring(0, contactsFile.getAbsolutePath().lastIndexOf("\\"));
        List<File> resultFiles = getFilesFromDir(absolutePathName, containtingName,".csv");
        int j = 1;
        for (File f: resultFiles) {
            System.out.println(j + "." + f.getName() + " last modified " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date(f.lastModified())));
            j++;

        }
        System.out.println();

    }
        public static long lastModified=0;

    @Override
    public void updateFromDataSource(){

        ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
            lastModified=contactsFile.lastModified();

            executorService.scheduleWithFixedDelay(()->{  // pt thread at fixed delay

                if (!((contactsFile.lastModified() ==lastModified))){
                    contacts.clear();
                    getContacts();
                    lastModified=(contactsFile.lastModified());
                }

                //System.out.println("Nothing changed");




            },1,10, TimeUnit.SECONDS);

        }

    public void showBackupMenu() {
        System.out.println("      BACKUP MENU     ");
        System.out.println("======================");
        System.out.println("1. View backup files  ");
        System.out.println("2. Restore from file  ");
        System.out.println("3. Purge old backups  ");
        System.out.println("4. Create backup now  ");
        System.out.println("0. EXIT               ");
        System.out.println("======================");

    }

    @Override
    public void backupDataMenu(){
        Scanner scanner=new Scanner(System.in);
        int backupOption;
        Map<Integer, String> fileMap = new HashMap<>();
        do {
            showBackupMenu();
            fileMap.clear();
            System.out.println();
            System.out.println("Input option: ");
            backupOption = scanner.nextInt();
            scanner.nextLine();
            String FullAbsolutePathName = (contactsFile.getAbsolutePath());
            String absolutePathName = FullAbsolutePathName.substring(0, FullAbsolutePathName.lastIndexOf("\\"));
            int j = 1;
            List<File> fileNames = getFilesFromDir(absolutePathName, "backup",".csv");

            for (File f: fileNames) {
                fileMap.put(j, f.getName());
                j++;
            }

            switch (backupOption) {
                case 1:
                    //list backup files, file names and last modified date, sorted oldest first
                    int x = 1;
                    fileNames = getFilesFromDir(absolutePathName, "backup",".csv");

                    for (File f: fileNames) {
                        System.out.println(x + "." + f.getName() + " last modified " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date(f.lastModified())));
                        x++;

                    }
                    System.out.println();

                    break;
                case 2:
                    //restore backups from file

                    System.out.println("restore backups from file");
                    printFileNames("backup");
                    System.out.println("Input index: ");
                    Integer backupIndex = scanner.nextInt();
                    scanner.nextLine();

                    Optional<Map.Entry<Integer, String>> result = fileMap.entrySet().stream()
                            .filter(integerStringEntry -> integerStringEntry.getKey().equals(backupIndex))
                            .findFirst();
                    Map.Entry<Integer, String> entryResult = null;
                    if (result.isPresent()) {
                        entryResult = result.get();
                    } else {
                        System.out.println("Nothing found");
                    }
                    String backupFileName = entryResult.getValue();
                    System.out.println("file name is: " + backupFileName);
                    restoreFromBackupFile(backupFileName);


                    break;
                case 3:
                    //purge old backups
                    System.out.println("How many of the last backup files do you want to keep?");
                    int keptFiles = scanner.nextInt();
                    scanner.nextLine();
                    List<File> backupFiles = getFilesFromDir(absolutePathName, "backup",".csv");
                    for (int i = backupFiles.size() - keptFiles - 1; i >= 0; i--) {
                        backupFiles.get(i).delete();
                    }
                    break;
                case 4:
                    //create backup now
                    System.out.println("Do you want to create a backup now? Y/N");
                    if (scanner.nextLine().equalsIgnoreCase("Y")) {
                        backupFile();
                    }


                case 0:
                    break;
                default:
                    System.out.println("Input only available options 1,2,3,4,0");
                    break;
            }

        } while (backupOption != 0);



    }





}
