package com.polezhaiev.carsharingapp.controller.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polezhaiev.carsharingapp.dto.car.CarDetailedInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CarSimpleInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import com.polezhaiev.carsharingapp.mapper.CarMapper;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.CarType;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.service.car.CarService;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected CarRepository carRepository;
    @Autowired
    protected CarService carService;
    @Autowired
    protected CarMapper carMapper;

    @BeforeAll
    static void setUp (
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
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

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all cars with simple info, should return all cars")
    @Sql(scripts = "classpath:database/car/01-add-two-cars-to-database.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getAll_ValidCars_ShouldReturnAllValidCars() throws Exception {
        List<CarSimpleInfoDto> expected = carService.findAll();

        MvcResult result = mockMvc.perform(
                        get("/api/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarSimpleInfoDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CarSimpleInfoDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.get(0).getBrand(), actual[0].getBrand());
        Assertions.assertEquals(expected.get(1).getModel(), actual[1].getModel());
        Assertions.assertEquals(expected.get(0).getTypeName(), actual[0].getTypeName());

        Arrays.stream(actual)
                .mapToLong(CarSimpleInfoDto::getId)
                .forEach(carRepository::deleteById);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all cars with detailed info, should return all cars")
    @Sql(scripts = "classpath:database/car/01-add-two-cars-to-database.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getCarsDetailedInfo_ValidCars_ShouldReturnAllValidCars() throws Exception {
        List<CarDetailedInfoDto> expected = carService.getCarsDetailedInfo();

        MvcResult result = mockMvc.perform(
                        get("/api/cars/")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDetailedInfoDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CarDetailedInfoDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.get(0).getBrand(), actual[0].getBrand());
        Assertions.assertEquals(expected.get(1).getModel(), actual[1].getModel());
        Assertions.assertEquals(expected.get(0).getTypeName(), actual[0].getTypeName());

        Arrays.stream(actual)
                .mapToLong(CarDetailedInfoDto::getId)
                .forEach(carRepository::deleteById);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all car by id, should return valid car")
    @Sql(scripts = "classpath:database/car/01-add-two-cars-to-database.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getCarById_ValidCar_ShouldReturnValidCar() throws Exception {
        List<CarDto> cars = carRepository.findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
        CarDto expected = cars.get(0);
        Long id = expected.getId();

        MvcResult result = mockMvc.perform(
                        get("/api/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        Assertions.assertEquals(expected, actual);

        carRepository.deleteById(id);
    }

    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Test
    @DisplayName("Update the car, should return updated car")
    @Sql(scripts = "classpath:database/car/01-add-two-cars-to-database.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateCarById_ValidRequestDto_ShouldReturnUpdatedCar() throws Exception {
        List<Car> cars = carRepository.findAll();
        Long id = cars.get(0).getId();

        CreateCarRequestDto requestDto = new CreateCarRequestDto()
                .setBrand("Maserati")
                .setDailyFee(10.5)
                .setInventory(1)
                .setModel("model")
                .setTypeName(CarType.TypeName.SEDAN.name());

        CarDto expected = new CarDto()
                .setId(id)
                .setBrand(requestDto.getBrand())
                .setDailyFee(requestDto.getDailyFee())
                .setInventory(requestDto.getInventory())
                .setModel(requestDto.getModel())
                .setTypeName(requestDto.getTypeName());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/api/cars/{id}", id)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        Assertions.assertEquals(expected, actual);

        cars.stream()
                .mapToLong(Car::getId)
                .forEach(carRepository::deleteById);
    }

    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Test
    @DisplayName("Delete the car by id, should return status isOk")
    @Sql(scripts = "classpath:database/car/01-add-two-cars-to-database.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteBook_ValidId_ShouldReturnNoContent() throws Exception {
        List<Car> cars = carRepository.findAll();
        Long id = cars.get(0).getId();

        MvcResult result = mockMvc.perform(
                        delete("/api/cars/{id}", id)
                )
                .andExpect(status().isOk())
                .andReturn();

        carRepository.deleteById(cars.get(1).getId());
    }
}
