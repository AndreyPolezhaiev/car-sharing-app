package com.polezhaiev.carsharingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import com.polezhaiev.carsharingapp.model.CarType;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.type.TypeRepository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected CarRepository carRepository;
    @Autowired
    protected TypeRepository typeRepository;

    @BeforeAll
    static void setUp (
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
        }
    }

    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Test
    @DisplayName("Create the car, should return the car")
    public void create_ValidRequestDto_ShouldReturnCar() throws Exception {
        CreateCarRequestDto requestDto = new CreateCarRequestDto()
                .setBrand("Toyota")
                .setDailyFee(10.5)
                .setInventory(1)
                .setModel("Camry")
                .setTypeName(CarType.TypeName.SEDAN.name());

        CarDto expected = new CarDto()
                .setModel(requestDto.getModel())
                .setInventory(requestDto.getInventory())
                .setBrand(requestDto.getBrand())
                .setDailyFee(requestDto.getDailyFee())
                .setTypeName(requestDto.getTypeName());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");

        carRepository.deleteById(actual.getId());
    }
}
