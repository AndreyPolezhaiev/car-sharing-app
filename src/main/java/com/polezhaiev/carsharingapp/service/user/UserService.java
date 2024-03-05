package com.polezhaiev.carsharingapp.service.user;

import com.polezhaiev.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
