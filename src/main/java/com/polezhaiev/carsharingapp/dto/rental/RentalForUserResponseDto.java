package com.polezhaiev.carsharingapp.dto.rental;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalForUserResponseDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Long carId;
}
