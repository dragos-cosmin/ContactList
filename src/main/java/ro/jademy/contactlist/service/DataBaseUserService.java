package ro.jademy.contactlist.service;

import org.apache.ibatis.jdbc.ScriptRunner;
import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataBaseUserService implements UserService {
    private String propertiesFileName;
    private List<User> contacts = new ArrayList<>();

    public DataBaseUserService(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    @Override
    public List<User> getContacts() {
        if (contacts.isEmpty()) {
            contacts.addAll(readFromDB());
        }
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

        //add contact to data base
        appendToDB(contact);

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

            // update user record in data base
            updateUserToDB(user);
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
            deleteUserFromDB(user);
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
                .filter(user -> user.getCompany().getName() != null)
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
        backupDB();
    }


    @Override
    public void updateFromDataSource(){
        ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(()->{  // pt thread at fixed delay

                contacts.clear();
                getContacts();

        },1,10, TimeUnit.SECONDS);
    }

    public void showBackupMenu() {
        System.out.println("        BACKUP MENU         ");
        System.out.println("============================");
        System.out.println("1. View backup dump files   ");
        System.out.println("2. Restore from dump file   ");
        System.out.println("3. Purge old backups        ");
        System.out.println("4. Create backup now        ");
        System.out.println("0. EXIT                     ");
        System.out.println("============================");

    }

    @Override
    public void backupDataMenu(){
        Scanner scanner=new Scanner(System.in);
        Object userService=new Object();
        int backupOption;
        Map<Integer, String> fileMap = new HashMap<>();
        do {
            showBackupMenu();
            System.out.println();
            System.out.println("Input option: ");
            backupOption = scanner.nextInt();
            scanner.nextLine();
            String FullAbsolutePathName = (new File(propertiesFileName).getAbsolutePath());
            String absolutePathName = FullAbsolutePathName.substring(0, FullAbsolutePathName.lastIndexOf("\\"));

            switch (backupOption) {
                case 1:
                    //list backup files, file names and last modified date, sorted oldest first
                    int j = 1;

                    List<File> fileNames = FileUserService.getFilesFromDir(absolutePathName, "backup",".sql");

                    for (File f: fileNames) {
                        System.out.println(j + "." + f.getName() + " last modified " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date(f.lastModified())));
                        fileMap.put(j, f.getName());
                        j++;

                    }
                    System.out.println();

                    break;
                case 2:
                    //restore backups from file
                    System.out.println("restore backups from file");
                    List<File> resultFiles=FileUserService.getFilesFromDir(absolutePathName,"backup",".sql");
                    int o = 1;
                    for (File f: resultFiles) {
                        System.out.println(o + "." + f.getName() + " last modified " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date(f.lastModified())));
                        o++;

                    }
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
                    restoreDBfromBackupFile(backupFileName);   // restore from backup SQL file


                    break;
                case 3:
                    //purge old backups
                    System.out.println("How many of the last backup files do you want to keep?");
                    int keptFiles = scanner.nextInt();
                    scanner.nextLine();
                    List<File> backupFiles = FileUserService.getFilesFromDir(absolutePathName, "backup",".sql");
                    for (int i = backupFiles.size() - keptFiles - 1; i >= 0; i--) {
                        backupFiles.get(i).delete();
                    }
                    break;
                case 4:
                    //create backup now
                    System.out.println("Do you want to create a backup now? Y/N");
                    if (scanner.nextLine().equalsIgnoreCase("Y")) {
                        backupDB();    // create a database backup
                    }


                case 0:
                    break;
                default:
                    System.out.println("Input only available options 1,2,3,4,0");
                    break;
            }

        } while (backupOption != 0);


    }

    private List<User> readFromDB() {
        Properties properties = getProperties(propertiesFileName);
        List<User> contactList = new ArrayList<>();
        List<Map<String, PhoneNumber>> phonesList = new ArrayList<>();
        List<Address> homeAdressList = new ArrayList<>();
        List<Company> companyList = new ArrayList<>();

        try {
            Connection userConnection = getConnection(properties);
            Statement userStatement = userConnection.createStatement();
            String userQuery = "SELECT * FROM " + properties.getProperty("db.name") + ".users;";

            ResultSet result = userStatement.executeQuery(userQuery);

            while (result.next()) {
                int userId = result.getInt("id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String email = result.getString("email");
                Integer age = result.getInt("age");
                String jobTitle = result.getString("job_title");
                boolean isFavorite = result.getBoolean("is_favorite");

                Statement phoneStatement = userConnection.createStatement();
                String phoneQuery = "SELECT phone_type, country_code, area_code, phonenumber FROM " + properties.getProperty("db.name") + ".phone_numbers\n" +
                        "WHERE user_id=" + userId + ";";
                ResultSet phoneResult = phoneStatement.executeQuery(phoneQuery);

                Map<String, PhoneNumber> phoneNumbers = new HashMap<>();

                while (phoneResult.next()) {
                    String phoneType = phoneResult.getString("phone_type");
                    String countryCode = phoneResult.getString("country_code");
                    String areaCode = phoneResult.getString("area_code").substring(1);
                    String phoneNumber = phoneResult.getString("phonenumber");
                    phoneNumbers.put(phoneType, new PhoneNumber(countryCode, areaCode, phoneNumber));
                }
                phonesList.add(phoneNumbers);

                Statement addressConnectionStatement = userConnection.createStatement();
                String addressQuerry = "SELECT adress_type, street_name, street_no,apart_no,floor,zip_code,city,country, companies.company_name FROM adresses\n" +
                        "LEFT JOIN companies\n" +
                        "ON adresses.user_id=companies.user_id\n" +
                        "WHERE adresses.user_id=" + userId + ";";

                ResultSet addressResult = addressConnectionStatement.executeQuery(addressQuerry);
                Address homeAdress = new Address();
                Address workAdress = new Address();
                Company company = new Company();

                while (addressResult.next()) {
                    String adresstype = addressResult.getString("adress_type");
                    String streetName = addressResult.getString("street_name");
                    Integer streetNumber = addressResult.getInt("street_no");
                    Integer apartNumber = addressResult.getInt("apart_no");
                    String floor = addressResult.getString("floor");
                    String zipCode = addressResult.getString("zip_code");
                    String city = addressResult.getString("city");
                    String country = addressResult.getString("country");
                    String compName = addressResult.getString("companies.company_name");

                    if (adresstype.equalsIgnoreCase("home")) {

                        homeAdress.setStreetName(streetName);
                        homeAdress.setStreetNumber(streetNumber);
                        homeAdress.setApartmentNumber(apartNumber);
                        homeAdress.setFloor(floor);
                        homeAdress.setZipCode(zipCode);
                        homeAdress.setCity(city);
                        homeAdress.setCountry(country);

                    } else {

                        workAdress.setStreetName(streetName);
                        workAdress.setStreetNumber(streetNumber);
                        workAdress.setApartmentNumber(apartNumber);
                        workAdress.setFloor(floor);
                        workAdress.setZipCode(zipCode);
                        workAdress.setCity(city);
                        workAdress.setCountry(country);
                        company.setName(compName);
                        company.setAddress(workAdress);

                    }
                }
                homeAdressList.add(homeAdress);
                companyList.add(company);


                User user = new User(firstName, lastName, email, age, phonesList.get(phonesList.size() - 1), homeAdressList.get(homeAdressList.size() - 1), jobTitle, companyList.get(companyList.size() - 1), isFavorite);
                user.setId(userId);
                contactList.add(user);
            }
            userConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactList;
    }

    private void appendToDB(User contact) {
        Properties properties = getProperties(propertiesFileName);
        try {
            Connection conn = getConnection(properties);
            Statement stm = conn.createStatement();
            String query = "INSERT INTO users\n" +
                    "VALUE(" + contact.getId() +
                    ",'" + contact.getFirstName() +
                    "','" + contact.getLastName() +
                    "','" + contact.getEmail() +
                    "'," + contact.getAge() +
                    ",'" + contact.getJobTitle() +
                    "'," + contact.isFavorite() + ");";
            stm.executeUpdate(query);
            for (Map.Entry<String, PhoneNumber> phoneNumberEntry: contact.getPhoneNumbers().entrySet()) {
                query = "INSERT INTO phone_numbers(user_id,phone_type,country_code,area_code,phonenumber)\n" +
                        "VALUES(" + contact.getId() + ",'" + phoneNumberEntry.getKey() + "','" + phoneNumberEntry.getValue().getCountryCode() + "','0" + phoneNumberEntry.getValue().getAreaCode() + "','" + phoneNumberEntry.getValue().getNumber() + "');";
                stm.executeUpdate(query);

            }
            query = "INSERT INTO adresses(user_id,adress_type,street_name,street_no,apart_no,floor,zip_code,city,country)\n" +
                    "VALUES(" + contact.getId() + ",'home','" + contact.getAddress().getStreetName() + "'," + contact.getAddress().getStreetNumber() + "," + contact.getAddress().getApartmentNumber() + ",'" + contact.getAddress().getFloor() + "','" + contact.getAddress().getZipCode() + "','" + contact.getAddress().getCity() + "','" + contact.getAddress().getCountry() + "'),\n" +
                    "(" + contact.getId() + ",'work','" + contact.getCompany().getAddress().getStreetName() +
                    "'," + contact.getCompany().getAddress().getStreetNumber() +
                    "," + contact.getCompany().getAddress().getApartmentNumber() +
                    ",'" + contact.getCompany().getAddress().getFloor() +
                    "','" + contact.getCompany().getAddress().getZipCode() +
                    "','" + contact.getCompany().getAddress().getCity() +
                    "','" + contact.getCompany().getAddress().getCountry() + "');";
            stm.executeUpdate(query);
            String queryId = "SELECT adresses.id FROM adresses\n" +
                    "WHERE user_id=" + contact.getId() + " AND adress_type='work';";
            ResultSet res2 = stm.executeQuery(queryId);
            int adrId = 0;
            while (res2.next()) {
                adrId = res2.getInt("adresses.id");
            }
            query = "INSERT INTO companies(user_id,company_name,adress_id)\n" +
                    "VALUE(" + contact.getId() + ",'" + contact.getCompany().getName() + "'," + adrId + ");";
            stm.executeUpdate(query);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }

    private void deleteUserFromDB(User contact) {
        Properties properties = getProperties(propertiesFileName);
        try {
            Connection connection = getConnection(properties);
            Statement statement = connection.createStatement();
            String querry = "DELETE FROM companies\n" +
                    "WHERE user_id=" + contact.getId() + ";";
            statement.executeUpdate(querry);

            querry = "DELETE FROM adresses\n" +
                    "WHERE user_id=" + contact.getId() + ";";
            statement.executeUpdate(querry);

            querry = "DELETE FROM phone_numbers\n" +
                    "WHERE user_id=" + contact.getId() + ";";
            statement.executeUpdate(querry);

            querry = "DELETE FROM users\n" +
                    "WHERE id=" + contact.getId() + ";";
            statement.executeUpdate(querry);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void updateUserToDB(User contact) {
        deleteUserFromDB(contact);
        appendToDB(contact);

    }

    public Connection getConnection(Properties props) {

        Properties connectionProps = new Properties();
        connectionProps.put("user", props.getProperty("db.user"));                  //connectionProps.put("user","someusername");
        connectionProps.put("password", props.getProperty("db.password"));               //connectionProps.put("password","userpassword");
        try {


            if (!checkDBexists(props)) {
                createDBfromSQL(props, "contactListCreate.sql", "contactListContactsPopulate.sql");
            }

            return DriverManager.getConnection(
                    "jdbc:" + "mysql" + "://" +
                            props.getProperty("db.connectionString") + ":" + props.getProperty("db.port") + "/" + props.getProperty("db.name") + "?useTimeZone=true&serverTimezone=EET",  //"server_location"+":"+"server_port"+"/database_name"+"?useTimeZone=true&serverTimezone=EET",
                    connectionProps);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
        }

        public static Properties getProperties (String propertiesFileName){
            Properties prop = new Properties();
            try (InputStream input = DataBaseUserService.class.getResourceAsStream("/" + propertiesFileName)) {
                prop.load(input);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return prop;
        }

        public boolean checkDBexists (Properties props){
            Properties connectionProps = new Properties();
            boolean DBexists = false;
            connectionProps.put("user", props.getProperty("db.user"));                  //connectionProps.put("user","someusername");
            connectionProps.put("password", props.getProperty("db.password"));               //connectionProps.put("password","userpassword");
            try {

                Connection servConn = DriverManager.getConnection("jdbc:" + "mysql" + "://" +
                                props.getProperty("db.connectionString") + ":" + props.getProperty("db.port") + "/" + "?useTimeZone=true&serverTimezone=EET",  //"server_location"+":"+"server_port"+"/database_name"+"?useTimeZone=true&serverTimezone=EET",
                        connectionProps);
                ResultSet resultSet = servConn.getMetaData().getCatalogs();
                while (resultSet.next()) {
                    String dataBaseName = resultSet.getString(1);
                    if (dataBaseName.equals(props.getProperty("db.name"))) {
                        DBexists = true;
                    }

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        return DBexists;
        }

        public void createDBfromSQL (Properties props,String SqlCreateDBFileName, String SqlPopulateDBFileName){
            Properties connectionProps = new Properties();
            connectionProps.put("user", props.getProperty("db.user"));                  //connectionProps.put("user","someusername");
            connectionProps.put("password", props.getProperty("db.password"));               //connectionProps.put("password","userpassword");
            try {

                Connection servConn = DriverManager.getConnection("jdbc:" + "mysql" + "://" +
                                props.getProperty("db.connectionString") + ":" + props.getProperty("db.port") + "/" + "?useTimeZone=true&serverTimezone=EET",  //"server_location"+":"+"server_port"+"/database_name"+"?useTimeZone=true&serverTimezone=EET",
                        connectionProps);
                ScriptRunner scriptRunner = new ScriptRunner(servConn);
                try {
                    Reader reader = new BufferedReader(new FileReader(SqlCreateDBFileName));
                    scriptRunner.runScript(reader);
                    Reader reader1 = new BufferedReader(new FileReader(SqlPopulateDBFileName));
                    scriptRunner.runScript(reader1);

                } catch (FileNotFoundException f) {
                    System.out.println(f.getCause());
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        public void backupDB(){
        Properties props=getProperties(propertiesFileName);
        String executeCmd="";
            String unicId = UUID.randomUUID().toString();
            executeCmd="mysqldump -u "+props.getProperty("db.user")+" -p"+props.getProperty("db.password")+" "+props.getProperty("db.name")+" -r backup"+unicId+".sql";
            try {
                Process runtimeProcess =Runtime.getRuntime().exec(executeCmd);
                int processComplete = runtimeProcess.waitFor();
                if(processComplete == 0){
                    System.out.println("Backup taken successfully");
                } else {
                    System.out.println("Could not take mysql backup");
                }

            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }

        }
            public void restoreDBfromBackupFile(String backupFileName){
                Properties props=getProperties(propertiesFileName);


                try {
                    Process runtimeProcess=Runtime.getRuntime().exec(new String[] {"cmd.exe","/c","mysql -u " +props.getProperty("db.user")+" -p"+props.getProperty("db.password")+" " +props.getProperty("db.name")+ " < "+backupFileName});
                    int processComplete =runtimeProcess.waitFor();
                //    System.out.println("exit value: "+runtimeProcess.exitValue());
                    if(processComplete == 0){
                        System.out.println("Restored successfully");
                    } else {
                        System.out.println("Could not restore from mysql backup");
                    }

                }catch (IOException | InterruptedException e){
                    e.printStackTrace();
                }




            }

    }
