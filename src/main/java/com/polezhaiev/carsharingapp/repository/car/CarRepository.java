package com.polezhaiev.carsharingapp.repository.car;

import com.polezhaiev.carsharingapp.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
