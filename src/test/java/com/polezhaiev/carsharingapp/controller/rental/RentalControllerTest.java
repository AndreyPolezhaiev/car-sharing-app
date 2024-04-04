package com.polezhaiev.carsharingapp.controller.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSetActualDateRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserLoginRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserLoginResponseDto;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.rental.RentalRepository;
import com.polezhaiev.carsharingapp.repository.user.UserRepository;
import com.polezhaiev.carsharingapp.security.AuthenticationService;
import com.polezhaiev.carsharingapp.security.CustomUserDetailsService;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected RentalRepository rentalRepository;
    @Autowired
    protected CarRepository carRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected CustomUserDetailsService customUserDetailsService;
    @Autowired
    protected AuthenticationService authenticationService;

    @BeforeAll
    static void setUp (
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Create the rental, should return the created rental")
    @Sql(scripts = {
            "classpath:database/car/01-add-one-car-to-database.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Rollback
    @Transactional
    public void createRental_ValidRequestDto_ShouldReturnRental() throws Exception {
        List<Car> cars = carRepository.findAll();

        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setPassword("1234");
        userLoginRequestDto.setEmail("admin@gmail.com");
        UserLoginResponseDto authenticatedUser = authenticationService.authenticate(userLoginRequestDto);
        String token = authenticatedUser.token();

        List<User> users = userRepository.findAll();
        User admin = users.get(0);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(admin.getEmail());
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

        RentalRequestDto requestDto = new RentalRequestDto();
        requestDto.setRentalDate(LocalDateTime.now());
        requestDto.setReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        requestDto.setCarId(cars.get(0).getId());
        requestDto.setActualReturnDate(null);

        RentalForUserResponseDto expected = new RentalForUserResponseDto();
        expected.setRentalDate(requestDto.getRentalDate());
        expected.setCarId(requestDto.getCarId());
        expected.setReturnDate(requestDto.getReturnDate());
        expected.setActualReturnDate(requestDto.getActualReturnDate());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/api/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalForUserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalForUserResponseDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Find all user rentals, should return all user's rentals")
    @Sql(scripts = {
            "classpath:database/car/01-add-one-car-to-database.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Rollback
    @Transactional
    public void findAllUserRentals_ByUserId_ShouldReturnAllRentals() throws Exception {
        List<Car> cars = carRepository.findAll();

        List<User> users = userRepository.findAll();
        User admin = users.get(0);

        Rental rental = new Rental();
        rental.setCar(cars.get(0));
        rental.setRentalDate(LocalDateTime.of(
                2024, 3, 8, 5, 5, 0)
        );
        rental.setReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActualReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActive(false);
        rental.setUser(admin);
        Rental savedRental = rentalRepository.save(rental);

        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setPassword("1234");
        userLoginRequestDto.setEmail("admin@gmail.com");
        UserLoginResponseDto authenticatedUser = authenticationService.authenticate(userLoginRequestDto);
        String token = authenticatedUser.token();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(admin.getEmail());
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

        RentalForUserResponseDto responseDto = new RentalForUserResponseDto();
        responseDto.setRentalDate(rental.getRentalDate());
        responseDto.setCarId(rental.getCar().getId());
        responseDto.setReturnDate(rental.getReturnDate());
        responseDto.setActualReturnDate(rental.getActualReturnDate());

        List<RentalForUserResponseDto> expected = List.of(responseDto);


                MvcResult result = mockMvc.perform(
                        get("/api/rentals/my")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalForUserResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalForUserResponseDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual[0].getId());
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.get(0).getCarId(), actual[0].getCarId());
        Assertions.assertEquals(expected.get(0).getRentalDate(), actual[0].getRentalDate());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Search rentals by userId and isActive, should return all valid rentals")
    @Sql(scripts = {
            "classpath:database/car/01-add-one-car-to-database.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Rollback
    @Transactional
    public void searchRentals_ByUserIdAndIsActive_ShouldReturnAllRentals() throws Exception {
        Long userId = 1L;
        boolean isActive = false;

        List<Car> cars = carRepository.findAll();

        List<User> users = userRepository.findAll();
        User admin = users.get(0);

        Rental rental = new Rental();
        rental.setCar(cars.get(0));
        rental.setRentalDate(LocalDateTime.of(
                2024, 3, 8, 5, 5, 0)
        );
        rental.setReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActualReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActive(false);
        rental.setUser(admin);
        Rental savedRental = rentalRepository.save(rental);

        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setId(savedRental.getId());
        responseDto.setRentalDate(rental.getRentalDate());
        responseDto.setCarId(rental.getCar().getId());
        responseDto.setReturnDate(rental.getReturnDate());
        responseDto.setActualReturnDate(rental.getActualReturnDate());
        responseDto.setActive(isActive);
        responseDto.setUserId(userId);

        List<RentalResponseDto> expected = List.of(responseDto);

        MvcResult result = mockMvc.perform(
                        get("/api/rentals/search")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalResponseDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual[0].getId());
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.get(0).getCarId(), actual[0].getCarId());
        Assertions.assertEquals(expected.get(0).getRentalDate(), actual[0].getRentalDate());
        Assertions.assertEquals(expected.get(0).getUserId(), actual[0].getUserId());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Set rental's actual return date")
    @Sql(scripts = {
            "classpath:database/car/01-add-one-car-to-database.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Rollback
    @Transactional
    public void setActualReturnDate_ByRentalId_ShouldReturnUpdatedRental() throws Exception {
        Long userId = 1L;
        boolean isActive = false;

        List<Car> cars = carRepository.findAll();

        List<User> users = userRepository.findAll();
        User admin = users.get(0);

        Rental rental = new Rental();
        rental.setCar(cars.get(0));
        rental.setRentalDate(LocalDateTime.of(
                2024, 3, 8, 5, 5, 0)
        );
        rental.setReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActive(true);
        rental.setUser(admin);
        Rental savedRental = rentalRepository.save(rental);
        Long rentalId = savedRental.getId();

        RentalResponseDto expected = new RentalResponseDto();
        expected.setId(savedRental.getId());
        expected.setRentalDate(rental.getRentalDate());
        expected.setCarId(rental.getCar().getId());
        expected.setReturnDate(rental.getReturnDate());
        expected.setActualReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        expected.setActive(isActive);
        expected.setUserId(userId);

        RentalSetActualDateRequestDto requestDto = new RentalSetActualDateRequestDto();
        requestDto.setActualDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/rentals/return/{rentalId}", rentalId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalResponseDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected, actual);
    }
}
