package com.polezhaiev.carsharingapp.service.car;

import com.polezhaiev.carsharingapp.dto.car.CarDetailedInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CarDto;
import com.polezhaiev.carsharingapp.dto.car.CarSimpleInfoDto;
import com.polezhaiev.carsharingapp.dto.car.CreateCarRequestDto;
import com.polezhaiev.carsharingapp.mapper.CarMapper;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.CarType;
import com.polezhaiev.carsharingapp.repository.car.CarRepository;
import com.polezhaiev.carsharingapp.repository.type.TypeRepository;
import com.polezhaiev.carsharingapp.service.car.impl.CarServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private TypeRepository typeRepository;
    @Mock
    private CarMapper carMapper;

    private static final Long ID = 1L;

    @Test
    @DisplayName("""
            Find all cars with simple info,
            should return all valid cars
            """)
    public void findAll_ValidCars_ShouldReturnAllValidCarsWithSimpleInfo() {
        Car car = new Car();
        car.setId(ID);

        CarSimpleInfoDto carSimpleInfoDto = new CarSimpleInfoDto();
        carSimpleInfoDto.setId(ID);

        List<CarSimpleInfoDto> expected = List.of(carSimpleInfoDto);
        List<Car> cars = List.of(car);

        Mockito.when(carRepository.findAll()).thenReturn(cars);
        Mockito.when(carMapper.toCarSimpleInfoDto(any())).thenReturn(carSimpleInfoDto);

        List<CarSimpleInfoDto> actual = carService.findAll();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Delete the car by id
        """)
    public void deleteById_WithValidId_ShouldDeleteCar() {
        carService.deleteById(ID);

        Mockito.verify(carRepository, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("""
            Find the car by valid id,
            should return valid car
        """)
    public void findById_WithValidId_ShouldReturnValidCar() {
        Car car = new Car();
        car.setId(ID);

        CarDto expected = new CarDto();
        expected.setId(ID);

        Mockito.when(carRepository.findById(any())).thenReturn(Optional.of(car));
        Mockito.when(carMapper.toDto(any())).thenReturn(expected);

        CarDto actual = carService.findById(ID);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Find the car by invalid id,
            should throw EntityNotFoundException
        """)
    public void findById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        String expected = "Can't find the car by id: " + ID;

        Mockito.when(carRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> carService.findById(ID)
        );

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Correct saving of the car in database,
            should return saved car
        """)
    public void save_ValidCar_ShouldReturnSavedCar() {
        CreateCarRequestDto requestDto = new CreateCarRequestDto();

        Car car = new Car();
        car.setId(ID);

        CarType carType = new CarType();
        carType.setName(CarType.TypeName.SEDAN.name());

        car.setType(carType);

        CarDto expected = new CarDto();
        expected.setId(ID);
        expected.setTypeName(CarType.TypeName.SEDAN.name());

        Mockito.when(carMapper.toModel(any())).thenReturn(car);
        Mockito.when(typeRepository.findTypeByName(any())).thenReturn(carType);
        Mockito.when(carRepository.save(any())).thenReturn(car);
        Mockito.when(carMapper.toDto(any())).thenReturn(expected);

        CarDto actual = carService.save(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        Update the car by valid id,
        should return updated car
        """)
    public void updateCarById_WithValidId_ShouldReturnUpdatedCar() {
        Car car = new Car();
        car.setId(ID);

        CarType carType = new CarType();
        carType.setName(CarType.TypeName.SEDAN.name());

        car.setType(carType);

        CarDto expected = new CarDto();
        expected.setId(ID);
        expected.setTypeName(CarType.TypeName.SEDAN.name());

        CreateCarRequestDto requestDto = new CreateCarRequestDto();

        Mockito.when(carMapper.toModel(any())).thenReturn(car);
        Mockito.when(carMapper.toDto(any())).thenReturn(expected);
        Mockito.when(carRepository.save(any())).thenReturn(car);

        CarDto actual = carService.updateCarById(ID, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Find all cars with detailed info,
            should return all valid cars
            """)
    public void getCarsDetailedInfo_ValidCars_ShouldReturnAllValidCarsWithDetailedInfo() {
        Car car = new Car();
        car.setId(ID);

        CarDetailedInfoDto carDetailedInfoDto = new CarDetailedInfoDto();
        carDetailedInfoDto.setId(ID);

        List<CarDetailedInfoDto> expected = List.of(carDetailedInfoDto);
        List<Car> cars = List.of(car);

        Mockito.when(carRepository.findAll()).thenReturn(cars);
        Mockito.when(carMapper.toCarDetailedInfoDto(any())).thenReturn(carDetailedInfoDto);

        List<CarDetailedInfoDto> actual = carService.getCarsDetailedInfo();

        assertEquals(expected, actual);
    }
}
