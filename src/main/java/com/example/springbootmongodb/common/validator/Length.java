package com.example.springbootmongodb.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
public @interface Length {
    String message() default "Length of {fieldName} must be equal or less than {max}";
    String fieldName();
    int max() default 255;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
