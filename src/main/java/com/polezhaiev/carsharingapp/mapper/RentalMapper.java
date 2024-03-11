package com.polezhaiev.carsharingapp.mapper;

import com.polezhaiev.carsharingapp.config.MapperConfig;
import com.polezhaiev.carsharingapp.dto.rental.RentalForUserResponseDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalRequestDto;
import com.polezhaiev.carsharingapp.dto.rental.RentalResponseDto;
import com.polezhaiev.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "user", ignore = true)
    Rental toModel(RentalRequestDto requestDto);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "carId", source = "car.id")
    RentalForUserResponseDto toDtoWithoutUserId(Rental rental);
}
