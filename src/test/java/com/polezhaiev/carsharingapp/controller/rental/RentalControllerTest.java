package com.polezhaiev.carsharingapp.controller.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserLoginRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserLoginResponseDto;
import com.polezhaiev.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.rental.RentalRepository;
import com.polezhaiev.carsharingapp.repository.user.UserRepository;
import com.polezhaiev.carsharingapp.security.AuthenticationService;
import com.polezhaiev.carsharingapp.security.CustomUserDetailsService;
import com.polezhaiev.carsharingapp.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    protected UserService userService;
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
//    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @DisplayName("Create the rental, should return the created rental")
    @Sql(scripts = {
            "classpath:database/rental/01-add-one-car-to-database.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createRental_ValidRequestDto_ShouldReturnRental() throws Exception {
        List<Car> cars = carRepository.findAll();

        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setEmail("alice@gmail.com");
        userRegistrationRequestDto.setPassword("1111");
        userRegistrationRequestDto.setFirstName("alice");
        userRegistrationRequestDto.setLastName("alison");
        userRegistrationRequestDto.setRepeatPassword("1111");
        userService.register(userRegistrationRequestDto);

        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setPassword(userRegistrationRequestDto.getPassword());
        userLoginRequestDto.setEmail(userRegistrationRequestDto.getEmail());
        UserLoginResponseDto authenticatedUser = authenticationService.authenticate(userLoginRequestDto);
        String token = authenticatedUser.token();

        List<User> users = userRepository.findAll();
        User alice = users.get(1);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(alice.getEmail());
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
                                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalForUserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalForUserResponseDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");

        carRepository.deleteById(cars.get(0).getId());
        rentalRepository.deleteById(actual.getId());
    }
}
