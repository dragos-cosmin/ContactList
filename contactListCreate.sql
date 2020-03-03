CREATE DATABASE contactlist;

USE contactlist;

CREATE TABLE users (
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
first_name VARCHAR(30),
last_name VARCHAR(30) NOT NULL,
email VARCHAR(30),
age INT,
job_title VARCHAR(20),
is_favorite BOOLEAN DEFAULT FALSE
);

CREATE TABLE phone_numbers(
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
user_id INT UNSIGNED,
phone_type ENUM('home','work','mobile'),
country_code VARCHAR(5) DEFAULT '+40',
area_code VARCHAR(5),
phonenumber VARCHAR(10) NOT NULL,
FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE adresses(
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
user_id INT UNSIGNED,
adress_type ENUM('home','work'),
street_name VARCHAR(30),
street_no INT,
apart_no INT,
floor VARCHAR(10),
zip_code VARCHAR(10),
city VARCHAR(20) NOT NULL,
country VARCHAR(20) DEFAULT 'Romania',
FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE companies(
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
user_id INT UNSIGNED,
company_name VARCHAR(50) NOT NULL,
adress_id INT UNSIGNED,
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (adress_id) REFERENCES adresses(id)
);
