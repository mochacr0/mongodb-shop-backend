package com.example.springbootmongodb.common.validator;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.repository.UserAddressRepository;
import com.example.springbootmongodb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAddressDataValidator extends DataValidator<UserAddress>{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Override
    protected void validateOnCreateImpl(UserAddress data) {
        long totalAddresses = userAddressRepository.count();
        if (totalAddresses > 5) {
            throw new InvalidDataException("Only 5 addresses are allowed in total.");
        }
    }

    @Override
    protected void validateOnUpdateImpl(UserAddress data) {

    }

    @Override
    protected void validateCommon(UserAddress data) {
//        if (StringUtils.isBlank(data.getUserId())) {
//            throw new InvalidDataException("User address should be specific");
//        }
//        Optional<UserEntity> userOptional = userRepository.findById(data.getUserId());
//        if (userOptional.isEmpty()) {
//            throw new InvalidDataException("User address should be assigned to an existing user");
//        }
    }
}
