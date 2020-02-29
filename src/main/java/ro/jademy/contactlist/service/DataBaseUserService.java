package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.sql.*;
import java.util.*;

public class DataBaseUserService implements UserService {
    private String dataBaseName;
    private String serverLocation;
    private String serverPort;
    private String userName;
    private String userPass;
    private List<User> contacts = new ArrayList<>();

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }


    public DataBaseUserService(String dataBaseName, String serverLocation, String serverPort, String userName, String userPass) {
        this.dataBaseName = dataBaseName;
        this.serverLocation = serverLocation;
        this.serverPort = serverPort;
        this.userName = userName;
        this.userPass = userPass;
    }

    @Override
    public List<User> getContacts() {
        if (contacts.isEmpty()) {
            contacts.addAll(readFromDB());
        }

        return contacts;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    @Override
    public Optional<User> getContactbyId(int userId) {
        Optional<User> userOpt = contacts.stream().filter(user -> user.getId() == userId).findFirst();

        return userOpt;
    }

    @Override
    public void addContact(User user) {

    }

    @Override
    public void editContact(int userId, String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, Address address, String jobTitle, Company company, boolean isFavorite) {

    }

    @Override
    public void removeContact(int userId) {

    }

    @Override
    public List<User> search(String query) {
        return null;
    }

    private List<User> readFromDB() {
        List<User> contactList = new ArrayList<>();
        List<Map<String,PhoneNumber>>phonesList=new ArrayList<>();
        List<Address>homeAdressList=new ArrayList<>();
        List<Company>companyList=new ArrayList<>();

        try {
            Connection conn = getConnection();

            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery("SELECT * FROM " + dataBaseName + ".users;");

            while (result.next()) {
                int userId = result.getInt("id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String email = result.getString("email");
                Integer age = result.getInt("age");
                String jobTitle = result.getString("job_title");
                Boolean isFavorite = result.getBoolean("is_favorite");

                try {
                    Connection conn2 = getConnection();

                    Statement stm2 = conn2.createStatement();

                    ResultSet result2 = stm2.executeQuery("SELECT phone_type, country_code, area_code, phonenumber FROM " + dataBaseName + ".phone_numbers\n"+
                            "WHERE user_id="+userId+";");
                    Map<String, PhoneNumber> phoneNumbers = new HashMap<>();
                    while (result2.next()) {
                        String phoneType = result2.getString("phone_type");
                        String countryCode = result2.getString("country_code");
                        String areaCode = result2.getString("area_code").substring(1);
                        String phoneNumber = result2.getString("phonenumber");
                        phoneNumbers.put(phoneType,new PhoneNumber(countryCode,areaCode,phoneNumber));
                    }
                    phonesList.add(phoneNumbers);
                    conn2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    Connection connection = getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT adress_type, street_name, street_no,apart_no,floor,zip_code,city,country, companies.company_name FROM adresses\n" +
                            "LEFT JOIN companies\n" +
                            "ON adresses.user_id=companies.user_id\n" +
                            "WHERE adresses.user_id="+userId+";");
                    Address homeAdress=new Address();
                    Address workAdress=new Address();
                    Company company=new Company();

                    while (resultSet.next()) {
                        String adresstype = resultSet.getString("adress_type");
                        String streetName = resultSet.getString("street_name");
                        Integer streetNumber = resultSet.getInt("street_no");
                        Integer apartNumber = resultSet.getInt("apart_no");
                        String floor = resultSet.getString("floor");
                        String zipCode = resultSet.getString("zip_code");
                        String city = resultSet.getString("city");
                        String country = resultSet.getString("country");
                        String compName = resultSet.getString("companies.company_name");

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
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                User user = new User(firstName,lastName,email,age,phonesList.get(phonesList.size()-1),homeAdressList.get(homeAdressList.size()-1),jobTitle,companyList.get(companyList.size()-1),isFavorite);
                user.setId(userId);
                contactList.add(user);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contactList;
    }


    public Connection getConnection() throws SQLException {

        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);                  //connectionProps.put("user","someusername");
        connectionProps.put("password", userPass);               //connectionProps.put("password","userpassword");

        Connection conn = DriverManager.getConnection(
                "jdbc:" + "mysql" + "://" +
                        serverLocation + ":" + serverPort + "/" + dataBaseName + "?useTimeZone=true&serverTimezone=EET",  //"server_location"+":"+"server_port"+"/database_name"+"?useTimeZone=true&serverTimezone=EET",
                connectionProps);
        return conn;

    }
}
