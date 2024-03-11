package com.polezhaiev.carsharingapp.repository.rental.spec;

import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.repository.SpecificationProvider;
import com.polezhaiev.carsharingapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationProviderManager implements SpecificationProviderManager<Rental> {
    private final List<SpecificationProvider<Rental>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Rental> getSpecification(Object key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find correct specification provider for key: " + key)
                );
    }
}
