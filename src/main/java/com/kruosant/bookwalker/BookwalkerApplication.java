package com.kruosant.bookwalker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;


@SpringBootApplication
public class BookwalkerApplication {
  static void main(String[] args) {
    SpringApplication.run(BookwalkerApplication.class, args);
  }

}
