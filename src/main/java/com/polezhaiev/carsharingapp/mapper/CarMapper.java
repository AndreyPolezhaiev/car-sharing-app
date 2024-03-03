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

    //    @AfterMapping
    //    default void setTypeName(@MappingTarget CarDto carDto, Car car) {
    //        String typeName = car.getType().getName();
    //        carDto.setTypeName(typeName);
    //    }

    @Mapping(target = "type", ignore = true)
    Car toModel(CreateCarRequestDto requestDto);

    @Mapping(target = "typeName", source = "type.name")
    CarSimpleInfoDto toCarSimpleInfoDto(Car car);

    //    @AfterMapping
    //    default void setTypeName(@MappingTarget CarSimpleInfoDto carDto, Car car) {
    //        String typeName = car.getType().getName();
    //        carDto.setTypeName(typeName);
    //    }

    @Mapping(target = "typeName", source = "type.name")
    CarDetailedInfoDto toCarDetailedInfoDto(Car car);

    //    @AfterMapping
    //    default void setTypeName(@MappingTarget CarDetailedInfoDto carDto, Car car) {
    //        String typeName = car.getType().getName();
    //        carDto.setTypeName(typeName);
    //    }
}
