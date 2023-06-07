package com.example.springbootmongodb.common.validator;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDataValidator extends DataValidator<User>{

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void validateOnCreateImpl(User data) {
        if (data.getId() != null) {
            throw new InvalidDataException(String.format("Cannot create user with id [%s]", data.getId()));
        }
    }

    @Override
    protected void validateOnUpdateImpl(User data) {
        if (data.getId() == null) {
            throw new InvalidDataException("User ID should be specified");
        }
        Optional<UserEntity> existingUser = userRepository.findById(data.getId());
        if (existingUser.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with id [%s] is not found", data.getId()));
        }
    }

    @Override
    protected void validateCommon(User data) {
        if (data.getName() != null) {
            User existingUserWithGivenName = DaoUtils.toData(userRepository.findByName(data.getName()));
            if (existingUserWithGivenName != null && !existingUserWithGivenName.getId().equals(data.getId())) {
                throw new InvalidDataException(String.format("User with name [%s] already exists", data.getName()));
            }
        }
        if (data.getEmail() != null) {
            User existingUserWithGivenEmail = DaoUtils.toData(userRepository.findByEmail(data.getEmail()));
            if (existingUserWithGivenEmail != null && !existingUserWithGivenEmail.getId().equals(data.getId())) {
                throw new InvalidDataException(String.format("User with email [%s] already exists", data.getEmail()));
            }
        }
//        if (data.getUserCredentials() == null) {
//            throw new InvalidDataException("User must have a user credentials");
//        }
    }
}
