package com.polezhaiev.carsharingapp.repository;

import com.polezhaiev.carsharingapp.dto.rental.RentalSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParametersDto searchParameters);
}
