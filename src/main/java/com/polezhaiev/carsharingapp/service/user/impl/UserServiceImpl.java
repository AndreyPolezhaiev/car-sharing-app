package com.polezhaiev.carsharingapp.service.user.impl;

import com.polezhaiev.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.polezhaiev.carsharingapp.dto.user.UserResponseDto;
import com.polezhaiev.carsharingapp.exception.RegistrationException;
import com.polezhaiev.carsharingapp.mapper.UserMapper;
import com.polezhaiev.carsharingapp.model.Role;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.repository.role.RoleRepository;
import com.polezhaiev.carsharingapp.repository.user.UserRepository;
import com.polezhaiev.carsharingapp.service.user.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("There is already user with such email");
        }
        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER.name());
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(role));
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
