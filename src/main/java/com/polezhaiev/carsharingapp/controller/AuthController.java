package com.polezhaiev.carsharingapp.controller;

import com.polezhaiev.carsharingapp.dto.user.UserLoginRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserLoginResponseDto;
import com.polezhaiev.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserResponseDto;
import com.polezhaiev.carsharingapp.exception.RegistrationException;
import com.polezhaiev.carsharingapp.security.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login the user", description = "Login the user")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @Operation(summary = "Register a new user", description = "Register a new user")
    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
