package com.example.springbootmongodb.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class StringLengthValidator implements ConstraintValidator<Length, String> {
    private int max;

    @Override
    public void initialize(Length constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s)) {
            return false;
        }
        return s.length() <= max;
    }
}
