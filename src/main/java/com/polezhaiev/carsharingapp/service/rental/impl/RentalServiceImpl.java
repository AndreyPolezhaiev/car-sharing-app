package com.polezhaiev.carsharingapp.service.rental.impl;

import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSetActualDateRequestDto;
import com.polezhaiev.carsharingapp.exception.app.EntityNotFoundException;
import com.polezhaiev.carsharingapp.exception.app.LackOfCarsException;
import com.polezhaiev.carsharingapp.exception.app.RentalIsNotActiveException;
import com.polezhaiev.carsharingapp.mapper.RentalMapper;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.rental.RentalRepository;
import com.polezhaiev.carsharingapp.repository.rental.spec.RentalSpecificationBuilder;
import com.polezhaiev.carsharingapp.repository.user.UserRepository;
import com.polezhaiev.carsharingapp.service.notification.NotificationService;
import com.polezhaiev.carsharingapp.service.rental.RentalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final RentalSpecificationBuilder specificationBuilder;
    private final NotificationService notificationService;

    @Override
    public RentalForUserResponseDto createRental(Long userId, RentalRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the car by id: " + requestDto.getCarId()
                )
        );
        if (car.getInventory() == 0) {
            throw new LackOfCarsException(
                    "There aren't available cars by id: " + requestDto.getCarId()
            );
        } else {
            car.setInventory(car.getInventory() - 1);
        }

        final User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the user by id: " + userId)
        );

        Rental rental = rentalMapper.toModel(requestDto);
        rental.setCar(car);
        rental.setActive(true);
        carRepository.save(car);
        rental.setUser(user);
        Rental saved = rentalRepository.save(rental);
        notificationService.sendMessageAboutNewRentalCreated(rental);
        return rentalMapper.toDtoWithoutUserId(saved);
    }

    @Override
    public List<RentalForUserResponseDto> findAllUserRentals(Long userId) {
        return rentalRepository.findAllByUserId(userId)
                .stream()
                .map(rentalMapper::toDtoWithoutUserId)
                .toList();
    }

    @Override
    public List<RentalResponseDto> searchRentals(RentalSearchParametersDto searchParameters) {
        Specification<Rental> specification = specificationBuilder.build(searchParameters);
        return rentalRepository.findAll(specification)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalResponseDto setActualReturnDate(
            Long rentalId, RentalSetActualDateRequestDto requestDto) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the rental by id: " + rentalId
                )
        );
        if (!rental.isActive()) {
            throw new RentalIsNotActiveException(
                    "Can't set actual date for the rental with id: "
                    + rental.getId()
                    + ", because this rental is not active");
        }
        rental.setActualReturnDate(requestDto.getActualDate());
        rental.setActive(false);

        Car car = carRepository.findById(rental.getCar().getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the car by id: " + rental.getCar().getId()
                )
        );
        car.setInventory(car.getInventory() + 1);

        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
