package com.example.springbootmongodb.common.validator;

import com.example.springbootmongodb.common.data.AbstractData;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public abstract class DataValidator<E extends AbstractEntity> {
    //separate validateOnCreate and validateOnUpdate
    public void validateOnCreate(E data) {
        if (data == null) {
            throw new InvalidDataException("Data object can't be null");
        }
        log.info("Perform data validation for creating data: " + data);
        ConstraintValidator.validateFields(data);
        validateOnCreateImpl(data);
        validateCommon(data);
    }

    public void validateOnUpdate(E data) {
        if (data == null) {
            throw new InvalidDataException("Data object can't be null");
        }
        log.info("Perform data validation for updating data: " + data);
        if (StringUtils.isBlank(data.getId())) {
            throw new InvalidDataException("Data ID should be specified");
        }
        ConstraintValidator.validateFields(data);
        validateOnUpdateImpl(data);
        validateCommon(data);
    }

    protected abstract void validateOnCreateImpl(E data);

    protected abstract void validateOnUpdateImpl(E data);

    protected abstract void validateCommon(E data);

}
