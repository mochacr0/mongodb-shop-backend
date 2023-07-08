package com.example.springbootmongodb.common.validator;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.repository.UserAddressRepository;
import com.example.springbootmongodb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAddressDataValidator extends DataValidator<UserAddressEntity>{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Override
    protected void validateOnCreateImpl(UserAddressEntity data) {
        long totalAddresses = userAddressRepository.countByUserId(data.getUserId());
        if (totalAddresses >= 5) {
            throw new InvalidDataException("Only 5 addresses are allowed per user.");
        }
    }

    @Override
    protected void validateOnUpdateImpl(UserAddressEntity data) {

    }

    @Override
    protected void validateCommon(UserAddressEntity data) {
//        if (StringUtils.isBlank(data.getUserId())) {
//            throw new InvalidDataException("User address should be specific");
//        }
//        Optional<UserEntity> userOptional = userRepository.findById(data.getUserId());
//        if (userOptional.isEmpty()) {
//            throw new InvalidDataException("User address should be assigned to an existing user");
//        }
    }
}
