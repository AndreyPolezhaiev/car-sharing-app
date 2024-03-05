package com.polezhaiev.carsharingapp.mapper;

import com.polezhaiev.carsharingapp.config.MapperConfig;
import com.polezhaiev.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserResponseDto;
import com.polezhaiev.carsharingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);
}
