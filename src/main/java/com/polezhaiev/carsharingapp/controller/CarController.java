package com.polezhaiev.carsharingapp.controller;

import com.polezhaiev.carsharingapp.dto.car.CarDetailedInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CarSimpleInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import com.polezhaiev.carsharingapp.service.car.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for cars managing")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Create new car", description = "Create new car")
    @PostMapping
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Get all cars", description = "Get all cars")
    @GetMapping
    public List<CarSimpleInfoDto> getAll() {
        return carService.findAll();
    }

    @Operation(summary = "Get all cars with detailed information",
            description = "Get all cars with detailed information")
    @GetMapping("/")
    public List<CarDetailedInfoDto> getCarsDetailedInfo() {
        return carService.getCarsDetailedInfo();
    }

    @Operation(summary = "Get the car by id", description = "Get the car by id")
    @GetMapping("/{id}")
    public CarDto getById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @Operation(summary = "Update the car", description = "Update the car")
    @PutMapping("/{id}")
    public CarDto updateCarById(@PathVariable Long id,
                                @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.updateCarById(id, requestDto);
    }

    @Operation(summary = "Delete the car by id", description = "Delete the car by id")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
