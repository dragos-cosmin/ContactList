package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
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

    private List<User> readFromDB() {
        Properties properties=getProperties(propertiesFileName);
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
        Properties properties=getProperties(propertiesFileName);
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
        Properties properties=getProperties(propertiesFileName);
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


    public Connection getConnection(Properties props) throws SQLException {

        Properties connectionProps = new Properties();
        connectionProps.put("user",props.getProperty("db.user") );                  //connectionProps.put("user","someusername");
        connectionProps.put("password", props.getProperty("db.password"));               //connectionProps.put("password","userpassword");

        return DriverManager.getConnection(
                "jdbc:" + "mysql" + "://" +
                        props.getProperty("db.connectionString") + ":" + props.getProperty("db.port") + "/" + props.getProperty("db.name") + "?useTimeZone=true&serverTimezone=EET",  //"server_location"+":"+"server_port"+"/database_name"+"?useTimeZone=true&serverTimezone=EET",
                connectionProps);

    }

    public static Properties getProperties(String propertiesFileName){
        Properties prop=new Properties();
        try (InputStream input=DataBaseUserService.class.getResourceAsStream("/"+propertiesFileName ) ){
            prop.load(input);

        } catch (IOException e){
            e.printStackTrace();
        }

        return prop;
    }

}

