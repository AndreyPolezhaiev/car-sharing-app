package com.polezhaiev.carsharingapp.dto.user;

import com.polezhaiev.carsharingapp.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@FieldMatch(first = "password", second = "repeatPassword")
public class UserRegistrationRequestDto {
    @Email
    private String email;
    private String password;
    private String repeatPassword;
    private String firstName;
    private String lastName;
}
