package com.polezhaiev.carsharingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, WebMvcAutoConfiguration.class})
@ComponentScan(basePackages = {"com.polezhaiev.carsharingapp"})
public class CarSharingAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarSharingAppApplication.class, args);
    }
}
