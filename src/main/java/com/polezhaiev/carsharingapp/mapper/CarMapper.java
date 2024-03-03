package com.polezhaiev.carsharingapp.mapper;

import com.polezhaiev.carsharingapp.config.MapperConfig;
import com.polezhaiev.carsharingapp.dto.car.CarDetailedInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CarSimpleInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import com.polezhaiev.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    @Mapping(target = "typeName", source = "type.name")
    CarDto toDto(Car car);

    @Mapping(target = "type", ignore = true)
    Car toModel(CreateCarRequestDto requestDto);

    @Mapping(target = "typeName", source = "type.name")
    CarSimpleInfoDto toCarSimpleInfoDto(Car car);

    @Mapping(target = "typeName", source = "type.name")
    CarDetailedInfoDto toCarDetailedInfoDto(Car car);
}
