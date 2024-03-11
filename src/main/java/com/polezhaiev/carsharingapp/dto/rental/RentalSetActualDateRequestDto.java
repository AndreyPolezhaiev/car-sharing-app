package com.polezhaiev.carsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalSetActualDateRequestDto {
    private LocalDate actualDate;
}
