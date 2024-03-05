package com.polezhaiev.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, final ConstraintValidatorContext context) {
        BeanWrapper wrapper = new BeanWrapperImpl(value);
        Object firstObj = wrapper.getPropertyValue(firstFieldName);
        Object secondObj = wrapper.getPropertyValue(secondFieldName);

        return Objects.equals(firstObj, secondObj);
    }
}
