package com.example.springbootmongodb.common.validator;

import com.example.springbootmongodb.common.data.AbstractData;
import com.example.springbootmongodb.exception.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public abstract class DataValidator<D extends AbstractData> {
    //separate validateOnCreate and validateOnUpdate
    public void validateOnCreate(D data) {
        if (data == null) {
            throw new InvalidDataException("Data object can't be null");
        }
        log.info("Perform data validation for creating data: " + data);
        ConstraintValidator.validateFields(data);
        validateOnCreateImpl(data);
        validateCommon(data);
    }

    public void validateOnUpdate(D data) {
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

    protected abstract void validateOnCreateImpl(D data);

    protected abstract void validateOnUpdateImpl(D data);

    protected abstract void validateCommon(D data);

}
