package com.example.springbootmongodb.common.validator;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;

import java.util.List;
import java.util.Set;

public class ConstraintValidator {
    private static Validator fieldsValidator;
    static {
        initializeValidator();
    }
    private static void initializeValidator() {
        HibernateValidatorConfiguration validatorConfiguration = Validation.byProvider(HibernateValidator.class).configure();
        ConstraintMapping constraintMapping = validatorConfiguration.createConstraintMapping();
        constraintMapping.constraintDefinition(Length.class).validatedBy(StringLengthValidator.class);
        constraintMapping.constraintDefinition(Required.class).validatedBy(StringRequiredValidator.class);
        validatorConfiguration.addMapping(constraintMapping);
        fieldsValidator = validatorConfiguration.buildValidatorFactory().getValidator();
    }
    public static void validateFields(Object object) {
        Set<ConstraintViolation<Object>> constraintViolations = fieldsValidator.validate(object);
        List<String> validationErrors = constraintViolations.stream().map(ConstraintViolation::getMessage).distinct().toList();
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation error: " + validationErrors.get(0));
        }
    }
}
