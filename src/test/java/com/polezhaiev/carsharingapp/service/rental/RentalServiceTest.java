package com.polezhaiev.carsharingapp.service.rental;

import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSetActualDateRequestDto;
import com.polezhaiev.carsharingapp.mapper.RentalMapper;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.rental.RentalRepository;
import com.polezhaiev.carsharingapp.repository.rental.spec.RentalSpecificationBuilder;
import com.polezhaiev.carsharingapp.repository.user.UserRepository;
import com.polezhaiev.carsharingapp.service.notification.NotificationService;
import com.polezhaiev.carsharingapp.service.rental.impl.RentalServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RentalSpecificationBuilder specificationBuilder;
    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("""
            Correct saving of the rental in database,
            should return saved rental
        """)
    public void createRental_ValidRequestDto_ShouldReturnSavedRental() {
        Long id = 1L;

        Rental rental = new Rental();
        rental.setId(id);

        Car car = new Car();
        car.setId(id);
        car.setInventory(1);

        User user = new User();
        user.setId(id);

        rental.setUser(user);
        rental.setCar(car);
        rental.setActive(true);

        RentalForUserResponseDto expected = new RentalForUserResponseDto();
        expected.setCarId(car.getId());
        expected.setId(id);

        Mockito.when(carRepository.findById(any())).thenReturn(Optional.of(car));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(rentalMapper.toModel(any())).thenReturn(rental);
        Mockito.when(rentalRepository.save(any())).thenReturn(rental);
        Mockito.when(rentalMapper.toDtoWithoutUserId(any())).thenReturn(expected);

        RentalForUserResponseDto actual =
                rentalService.createRental(user.getId(), new RentalRequestDto());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should throw LackOfCarsException
        """)
    public void createRental_ValidRequestDto_ShouldThrowLackOfCarsException() {
        Long id = 1L;

        Car car = new Car();
        car.setId(id);
        car.setInventory(0);

        Mockito.when(carRepository.findById(any())).thenReturn(Optional.of(car));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> rentalService.createRental(id, new RentalRequestDto().setCarId(id))
        );

        String actual = exception.getMessage();
        String expected = "There aren't available cars by id: " + id;

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should throw EntityNotFoundException
        """)
    public void createRental_ValidRequestDto_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        Car car = new Car();
        car.setId(id);
        car.setInventory(1);

        Mockito.when(carRepository.findById(any())).thenReturn(Optional.of(car));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> rentalService.createRental(id, new RentalRequestDto().setCarId(id))
        );

        String actual = exception.getMessage();
        String expected = "Can't find the user by id: " + id;

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Find all user's rentals,
            should return valid rentals
        """)
    public void findAllUserRentals_ValidUserId_ShouldReturnAllRentals() {
        Long id = 1L;

        Rental rental = new Rental();
        rental.setId(id);

        Car car = new Car();
        car.setId(id);
        car.setInventory(1);

        User user = new User();
        user.setId(id);

        rental.setUser(user);
        rental.setCar(car);
        rental.setActive(true);

        RentalForUserResponseDto responseDto = new RentalForUserResponseDto();
        responseDto.setCarId(car.getId());
        responseDto.setId(id);

        List<Rental> rentals = List.of(rental);
        List<RentalForUserResponseDto> expected = List.of(responseDto);

        Mockito.when(rentalRepository.findAllByUserId(any())).thenReturn(rentals);
        Mockito.when(rentalMapper.toDtoWithoutUserId(any())).thenReturn(responseDto);

        List<RentalForUserResponseDto> actual = rentalService.findAllUserRentals(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Search rentals by search parameters,
            should return all valid rentals
        """)
    public void searchRentals_BySearchParameters_ShouldReturnAllValidRentals() {
        Long id = 1L;

        Specification<Rental> specification = Mockito.mock(Specification.class);

        Rental rental = new Rental();
        rental.setId(id);

        Car car = new Car();
        car.setId(id);
        car.setInventory(1);

        User user = new User();
        user.setId(id);

        rental.setUser(user);
        rental.setCar(car);
        rental.setActive(true);

        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setCarId(car.getId());
        responseDto.setId(id);

        RentalSearchParametersDto searchParameters =
                new RentalSearchParametersDto(new Long[]{}, new Boolean[]{});

        List<Rental> rentals = List.of(rental);
        List<RentalResponseDto> expected = List.of(responseDto);

        Mockito.when(specificationBuilder.build(any())).thenReturn(specification);
        Mockito.when(rentalRepository.findAll(specification)).thenReturn(rentals);
        Mockito.when(rentalMapper.toDto(any())).thenReturn(responseDto);

        List<RentalResponseDto> actual = rentalService.searchRentals(searchParameters);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Set actual return date, should return rental
        """)
    public void setActualReturnDate_ValidRequestDto_ShouldReturnSavedRental() {
        Long id = 1L;

        Rental rental = new Rental();
        rental.setId(id);

        Car car = new Car();
        car.setId(id);
        car.setInventory(1);

        User user = new User();
        user.setId(id);

        rental.setUser(user);
        rental.setCar(car);
        rental.setActive(true);

        RentalResponseDto expected = new RentalResponseDto();
        expected.setCarId(car.getId());
        expected.setId(id);

        Mockito.when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));
        Mockito.when(carRepository.findById(any())).thenReturn(Optional.of(car));
        Mockito.when(rentalMapper.toDto(any())).thenReturn(expected);

        RentalResponseDto actual =
                rentalService.setActualReturnDate(user.getId(), new RentalSetActualDateRequestDto());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should throw EntityNotFoundException
        """)
    public void setActualReturnDate_InValidId_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        Mockito.when(rentalRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> rentalService.setActualReturnDate(id, new RentalSetActualDateRequestDto())
        );

        String actual = exception.getMessage();
        String expected = "Can't find the rental by id: " + id;

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should throw RentalIsNotActiveException
        """)
    public void setActualReturnDate_InvalidRequestDto_ShouldThrowRentalIsNotActiveException() {
        Long id = 1L;

        Rental rental = new Rental();
        rental.setId(id);
        rental.setActive(false);

        Mockito.when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> rentalService.setActualReturnDate(id, new RentalSetActualDateRequestDto())
        );

        String actual = exception.getMessage();
        String expected = "Can't set actual date for the rental with id: "
                + rental.getId()
                + ", because this rental is not active";

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should throw RentalIsNotActiveException
        """)
    public void setActualReturnDate_InvalidRequestDto_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        Car car = new Car();
        car.setId(id);
        car.setInventory(1);

        Rental rental = new Rental();
        rental.setId(id);
        rental.setActive(true);
        rental.setCar(car);

        Mockito.when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));
        Mockito.when(carRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> rentalService.setActualReturnDate(id, new RentalSetActualDateRequestDto())
        );

        String actual = exception.getMessage();
        String expected = "Can't find the car by id: " + rental.getCar().getId();

        assertEquals(expected, actual);
    }
}
