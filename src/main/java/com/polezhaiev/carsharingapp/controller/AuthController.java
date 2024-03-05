package com.polezhaiev.carsharingapp.controller;

import com.polezhaiev.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserResponseDto;
import com.polezhaiev.carsharingapp.exception.RegistrationException;
import com.polezhaiev.carsharingapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {
    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Register a new user")
    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
