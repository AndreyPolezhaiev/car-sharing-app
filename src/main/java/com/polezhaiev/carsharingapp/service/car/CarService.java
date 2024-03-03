package com.polezhaiev.carsharingapp.service.car;

import com.polezhaiev.carsharingapp.dto.car.CarDetailedInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CarSimpleInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import java.util.List;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    List<CarSimpleInfoDto> findAll();

    List<CarDetailedInfoDto> getCarsDetailedInfo();

    CarDto updateCarById(Long id, CreateCarRequestDto requestDto);

    void deleteById(Long id);

    CarDto findById(Long id);
}
