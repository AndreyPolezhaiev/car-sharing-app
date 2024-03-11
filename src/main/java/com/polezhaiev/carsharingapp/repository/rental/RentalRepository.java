package com.polezhaiev.carsharingapp.repository.rental;

import com.polezhaiev.carsharingapp.model.Rental;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository
        extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    List<Rental> findAllByUserId(Long id);
}
