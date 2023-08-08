package com.example.springbootmongodb.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberRequiredValidator implements ConstraintValidator<Required, Number> {
    @Override
    public boolean isValid(Number number, ConstraintValidatorContext constraintValidatorContext) {
        return number != null;
    }
}
