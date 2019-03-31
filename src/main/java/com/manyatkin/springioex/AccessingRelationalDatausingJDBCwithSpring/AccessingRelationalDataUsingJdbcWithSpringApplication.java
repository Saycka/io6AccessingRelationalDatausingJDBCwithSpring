package com.manyatkin.springioex.AccessingRelationalDatausingJDBCwithSpring;

import com.manyatkin.springioex.AccessingRelationalDatausingJDBCwithSpring.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//Spring Boot supports H2, an in-memory relational database engine,
// and automatically creates a connection.
// Because we are using spring-jdbc, Spring Boot automatically creates a JdbcTemplate.
// The @Autowired JdbcTemplate field automatically loads it and makes it available.
//
// This Application class implements Spring Boot’s CommandLineRunner,
// which means it will execute the run() method after the application context is loaded up.
//
//    First, you install some DDL using JdbcTemplate’s `execute method.
//
//Second, you take a list of strings and using Java 8 streams,
// split them into firstname/lastname pairs in a Java array.
//
//Then you install some records in your newly created table using JdbcTemplate’s `batchUpdate method.
// The first argument to the method call is the query string,
// the last argument (the array of Object s) holds the variables to be substituted into the query where the “?” characters are.

//For single insert statements, JdbcTemplate’s `insert method is good.
// But for multiple inserts, it’s better to use batchUpdate.

//Use ? for arguments to avoid SQL injection attacks by instructing JDBC to bind variables.


//Finally you use the query method to search your table for records matching the criteria.
// You again use the “?” arguments to create parameters for the query, passing in the actual values when you make the call.
// The last argument is a Java 8 lambda used to convert each result row into a new Customer object.
//
//Java 8 lambdas map nicely onto single method interfaces, like Spring’s RowMapper.
// If you are using Java 7 or earlier, you can easily plug in an anonymous interface implementation
// and have the same method body as the lambda expresion’s body contains, and it will work with no fuss from Spring.


@SpringBootApplication
public class AccessingRelationalDataUsingJdbcWithSpringApplication implements CommandLineRunner {

  private final static Logger logger = LoggerFactory
      .getLogger(AccessingRelationalDataUsingJdbcWithSpringApplication.class);

  private final String customersTable = "customers";
  private final String customerIdField = "id";
  private final String customerFirstNameField = "first_name";
  private final String customerLastNameField = "last_name";

  @Autowired
  JdbcTemplate jdbcTemplate;

  public static void main(String[] args) {
    SpringApplication.run(AccessingRelationalDataUsingJdbcWithSpringApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("create a table");

    jdbcTemplate.execute("DROP TABLE " + customersTable + " IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE " + customersTable + "(" +
        customerIdField + " SERIAL, " +
        customerFirstNameField + " VARCHAR(255), " +
        customerLastNameField + " VARCHAR (255))");

    List<Object[]> splitUpNames = Arrays.asList("Andrey Ivanov", "Andrey Smirnov", "Vasya Pupkin")
        .stream().map(name -> name.split(" ")).collect(Collectors.toList());

    splitUpNames.forEach(name -> logger.info(String.format("Insert record %s %s", name[0], name[1])));

    jdbcTemplate.batchUpdate("INSERT INTO " + customersTable + "(" +
        customerFirstNameField + ", " +
        customerLastNameField +
        ") VALUES (?,?)", splitUpNames);

    logger.info("Querying records where first_name = Andrey:");

    jdbcTemplate.query("SELECT " + customerIdField + ", " + customerFirstNameField + ", " + customerLastNameField
            + " FROM " + customersTable + " WHERE " + customerFirstNameField + "= ?",
        new Object[]{"Andrey"},
        (rs, rowNum) -> new Customer(rs.getInt(customerIdField), rs.getString(customerFirstNameField),
            rs.getString(customerLastNameField))).forEach(customer -> logger.info(customer.toString()));
  }
}
