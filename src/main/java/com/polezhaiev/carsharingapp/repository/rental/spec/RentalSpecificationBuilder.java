package com.polezhaiev.carsharingapp.repository.rental.spec;

import com.polezhaiev.carsharingapp.dto.rental.RentalSearchParametersDto;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.repository.SpecificationBuilder;
import com.polezhaiev.carsharingapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchParametersDto searchParameters) {
        Specification<Rental> spec = Specification.where(null);
        if (searchParameters.userIds() != null && searchParameters.userIds().length > 0) {
            spec = spec.and(rentalSpecificationProviderManager.getSpecification("user.id")
                    .getSpecification(searchParameters.userIds()));
        }
        if (searchParameters.isActives() != null && searchParameters.isActives().length > 0) {
            spec = spec.and(rentalSpecificationProviderManager.getSpecification("isActive")
                    .getSpecification(searchParameters.isActives()));
        }
        return spec;
    }
}
