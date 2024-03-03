package com.polezhaiev.carsharingapp.repository.type;

import com.polezhaiev.carsharingapp.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    Type findTypeByName(String name);
}
