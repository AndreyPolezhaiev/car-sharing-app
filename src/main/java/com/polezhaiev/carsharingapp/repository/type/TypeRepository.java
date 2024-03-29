package com.polezhaiev.carsharingapp.repository.type;

import com.polezhaiev.carsharingapp.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<CarType, Long> {
    CarType findTypeByName(String name);
}
