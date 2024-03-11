package com.polezhaiev.carsharingapp.service.rental;

import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSetActualDateRequestDto;
import java.util.List;

public interface RentalService {
    RentalForUserResponseDto createRental(Long userId, RentalRequestDto requestDto);

    List<RentalForUserResponseDto> findAllUserRentals(Long userId);

    List<RentalResponseDto> searchRentals(RentalSearchParametersDto searchParameters);

    RentalResponseDto setActualReturnDate(Long rentalId, RentalSetActualDateRequestDto requestDto);
}
