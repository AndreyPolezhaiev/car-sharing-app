package com.polezhaiev.carsharingapp.repository.role;

import com.polezhaiev.carsharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}