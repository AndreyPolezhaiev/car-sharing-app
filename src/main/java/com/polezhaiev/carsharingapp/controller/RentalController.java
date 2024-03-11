package com.polezhaiev.carsharingapp.controller;

import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalSetActualDateRequestDto;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Create a new rental for user",
            description = "Create a new rental for user")
    @PostMapping
    public RentalForUserResponseDto createRental(
            @RequestBody RentalRequestDto requestDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.createRental(user.getId(), requestDto);
    }

    @Operation(summary = "Get user's rentals",
            description = "Get user's rentals")
    @GetMapping("/my")
    public List<RentalForUserResponseDto> findAllUserRentals(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.findAllUserRentals(user.getId());
    }

    @Operation(summary = "Search rentals by userId and isActive",
            description = "Search rentals by userId and isActive")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/search")
    public List<RentalResponseDto> searchRentals(RentalSearchParametersDto searchParameters) {
        return rentalService.searchRentals(searchParameters);
    }

    @Operation(summary = "Set actual rental's return date",
            description = "Set actual rental's return date")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/return/{rentalId}")
    public RentalResponseDto setActualReturnDate(
            @PathVariable Long rentalId, @RequestBody RentalSetActualDateRequestDto requestDto) {
        return rentalService.setActualReturnDate(rentalId, requestDto);
    }
}
