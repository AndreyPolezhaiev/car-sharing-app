package com.polezhaiev.carsharingapp.service.car.impl;

import com.polezhaiev.carsharingapp.dto.car.CarDetailedInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CarSimpleInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import com.polezhaiev.carsharingapp.exception.app.EntityNotFoundException;
import com.polezhaiev.carsharingapp.mapper.CarMapper;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.CarType;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.type.TypeRepository;
import com.polezhaiev.carsharingapp.service.car.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final TypeRepository typeRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarSimpleInfoDto> findAll() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toCarSimpleInfoDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public CarDto findById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find the car by id: " + id));
        return carMapper.toDto(car);
    }

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        CarType type = typeRepository.findTypeByName(requestDto.getTypeName());
        car.setType(type);
        Car saved = carRepository.save(car);
        return carMapper.toDto(saved);
    }

    @Override
    public CarDto updateCarById(Long id, CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        car.setId(id);
        CarType type = typeRepository.findTypeByName(requestDto.getTypeName());
        car.setType(type);
        Car updated = carRepository.save(car);
        return carMapper.toDto(updated);
    }

    @Override
    public List<CarDetailedInfoDto> getCarsDetailedInfo() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toCarDetailedInfoDto)
                .toList();
    }
}
