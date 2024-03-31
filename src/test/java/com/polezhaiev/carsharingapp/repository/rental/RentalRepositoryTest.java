package com.polezhaiev.carsharingapp.repository.rental;

import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {
    @Autowired
    protected CarRepository carRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RentalRepository rentalRepository;

    @Test
    @DisplayName("""
            Find all rentals by user's id,
            should return all valid rentals
        """)
    @Sql(scripts = {
            "classpath:database/rental/01-add-one-car-to-database.sql",
            "classpath:database/rental/02-add-one-user-to-database.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllByCategoriesId_WithValidIds_ShouldReturnAllValidBooks() {
        Long userId = 2L;

        List<Car> cars = carRepository.findAll();
        Optional<User> user = userRepository.findById(userId);
        Rental rental = new Rental();
        rental.setCar(cars.get(0));
        rental.setRentalDate(LocalDateTime.now());
        rental.setReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActualReturnDate(LocalDateTime.of(
                2024, 4, 8, 5, 5, 0)
        );
        rental.setActive(false);
        rental.setUser(user.get());

        Rental saved = rentalRepository.save(rental);

        List<Rental> actual = rentalRepository.findAllByUserId(userId);

        assertEquals(1, actual.size());
        assertEquals(saved, actual.get(0));

        rentalRepository.deleteById(saved.getId());
        carRepository.deleteById(cars.get(0).getId());
        userRepository.deleteById(userId);
    }
}
