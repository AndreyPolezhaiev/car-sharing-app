package com.polezhaiev.carsharingapp.repository.rental.spec;

import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.JoinType;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return "user.id";
    }

    public Specification<Rental> getSpecification(Object[] params) {
        return (root, query, criteriaBuilder) ->
            root.join("user", JoinType.INNER).get("id").in(Arrays.asList(params));
    }
}
