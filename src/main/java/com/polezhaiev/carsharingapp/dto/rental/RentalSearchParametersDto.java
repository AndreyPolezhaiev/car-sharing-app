package com.polezhaiev.carsharingapp.dto.rental;

public record RentalSearchParametersDto(Long[] userIds, Boolean[] isActives) {
}
