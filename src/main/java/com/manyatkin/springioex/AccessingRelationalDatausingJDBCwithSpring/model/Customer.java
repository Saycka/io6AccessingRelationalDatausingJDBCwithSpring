package com.manyatkin.springioex.AccessingRelationalDatausingJDBCwithSpring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Customer {
    private int id;
    private String FirstName;
    private String SecondName;

}
