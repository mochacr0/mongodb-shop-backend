package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.common.validator.DataValidator;
import com.example.springbootmongodb.common.validator.UserAddressDataValidator;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.repository.UserAddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.util.List;

@Service
@Slf4j
public class UserAddressServiceImpl extends DataBaseService<UserAddress, UserAddressEntity> implements UserAddressService {
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DataValidator<UserAddress> userAddressDataValidator;

    @Override
    public MongoRepository<UserAddressEntity, String> getRepository() {
        return this.userAddressRepository;
    }

//    @Override
//    public Class getEntityClass() {
//        return UserAddressEntity.class;
//    }

    @Override
    @Transactional
    public UserAddress create(UserAddress userAddress) {
        log.info("Performing UserAddressService create");
        User existingUser = userService.findById(getCurrentUser().getId());
        if (existingUser == null) {
            throw new ItemNotFoundException("Cannot find current user");
        }
        userAddress.setUserId(existingUser.getId());
        UserAddress createdUserAddress = super.insert(userAddress);
        if (createdUserAddress.isDefault()) {
            existingUser.setDefaultAddressId(createdUserAddress.getId());
            userService.save(existingUser);
        }
        return createdUserAddress;
    }

    @Override
    @Transactional
    public UserAddress save(String addressId, UserAddress userAddress) {
        log.info("Performing UserAddressService save");
        User existingUser = userService.findById(getCurrentUser().getId());
        if (existingUser == null) {
            throw new ItemNotFoundException("Cannot find current user");
        }
        UserAddress existingAddress = this.findById(addressId);
        if (existingAddress == null) {
            throw new ItemNotFoundException(String.format("User address with id [%s] is not found", addressId));
        }
        if (!existingUser.getId().equals(existingAddress.getUserId())) {
            throw new AuthenticationServiceException("User ID mismatched. You aren't authorized to update this address!");
        }
        userAddress.setId(addressId);
        userAddress.setUserId(existingUser.getId());
        UserAddress savedUserAddress = super.save(userAddress);
        if (savedUserAddress.isDefault()) {
            existingUser.setDefaultAddressId(savedUserAddress.getId());
            userService.save(existingUser);
        }
        return savedUserAddress;
    }

    @Override
    public List<UserAddress> findUserAddressesByUserId(String userId) {
        log.info("Performing UserAddressService findUserAddressesByUserId");
        User user = userService.findById(userId);
        if (user == null) {
            throw new ItemNotFoundException(String.format("User with id [%s] is not found", userId));
        }
        List<UserAddress> userAddresses = DaoUtils.toListData(userAddressRepository.findByUserId(userId));
        for (UserAddress userAddress : userAddresses) {
            if (userAddress.getId().equals(user.getDefaultAddressId())) {
                userAddress.setDefault(true);
                break;
            }
        }
        return userAddresses;
    }

    @Override
    public List<UserAddress> findCurrentUserAddresses() {
        log.info("Performing UserAddressService findCurrentUserAddresses");
        return findUserAddressesByUserId(getCurrentUser().getId());
    }

    @Override
    public void deleteById(String addressId) {
        log.info("Performing UserAddressService deleteById");
        User existingUser = userService.findById(getCurrentUser().getId());
        if (existingUser == null) {
            return;
        }
        UserAddress existingAddress = this.findById(addressId);
        if (existingAddress == null) {
            return;
        }
        if (!existingUser.getId().equals(existingAddress.getUserId())) {
            throw new AuthenticationServiceException("User ID mismatched. You aren't authorized to delete this address!");
        }
        if (existingUser.getDefaultAddressId().equals(existingAddress.getId())) {
            throw new InvalidDataException("Cannot delete default address. Please change the default address and try again.");
        }
        userAddressRepository.deleteById(addressId);
    }

    @Override
    public UserAddress findById(String addressId) {
        log.info("Performing UserAddressService findById");
        return DaoUtils.toData(userAddressRepository.findById(addressId));
    }
}
