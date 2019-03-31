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
