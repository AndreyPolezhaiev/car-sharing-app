package com.polezhaiev.carsharingapp.dto.rental;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalSetActualDateRequestDto {
    private LocalDateTime actualDate;
}
