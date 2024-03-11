package com.polezhaiev.carsharingapp.repository.rental.spec;

import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return "isActive";
    }

    public Specification<Rental> getSpecification(Object[] params) {
        return (root, query, criteriaBuilder) ->
                root.get("isActive").in(Arrays.stream(params).toArray());
    }
}
