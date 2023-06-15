package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.UserAddressRequest;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.common.validator.DataValidator;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.repository.UserAddressRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAddressServiceImpl extends DataBaseService<UserAddress, UserAddressEntity> implements UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final DataValidator<UserAddress> userAddressDataValidator;
    @Autowired
    @Lazy
    private UserService userService;

    public static final String MISMATCHED_USER_IDS_MESSAGE = "User ID mismatched. You aren't authorized to perform this address!";
    public static final String DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE = "This address's default label is unchangeable. Set another default address first";

    @Override
    public MongoRepository<UserAddressEntity, String> getRepository() {
        return this.userAddressRepository;
    }

    @Override
    @Transactional
    public UserAddress create(UserAddressRequest userAddressRequest) {
        log.info("Performing UserAddressService create");
        UserAddress userAddress = new UserAddress(userAddressRequest);
        User existingUser = userService.findById(getCurrentUser().getId());
        userAddress.setUserId(existingUser.getId());
        userAddressDataValidator.validateOnCreate(userAddress);
        UserAddress createdUserAddress = super.insert(userAddress);
        if (userAddress.isDefault()) {
            existingUser.setDefaultAddressId(createdUserAddress.getId());
            userService.save(existingUser);
        }
        return createdUserAddress;
    }

    @Override
    @Transactional
    public UserAddress save(String addressId, UserAddressRequest userAddressRequest) {
        log.info("Performing UserAddressService save");
        UserAddress userAddress = new UserAddress(userAddressRequest);
        User existingUser = userService.findById(getCurrentUser().getId());
        UserAddress existingAddress = this.findById(addressId);
        if (!existingUser.getId().equals(existingAddress.getUserId())) {
            throw new InvalidDataException(MISMATCHED_USER_IDS_MESSAGE);
        }
        userAddress.setId(addressId);
        userAddress.setUserId(existingUser.getId());
        UserAddress savedUserAddress = super.save(userAddress);
        if (userAddress.isDefault()) {
            existingUser.setDefaultAddressId(savedUserAddress.getId());
            userService.save(existingUser);
        }
        else {
            if (StringUtils.isNotEmpty(existingUser.getDefaultAddressId()) && existingUser.getDefaultAddressId().equals(userAddress.getId())) {
                throw new InvalidDataException(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE);
            }
        }
        return savedUserAddress;
    }

    @Override
    public List<UserAddress> findUserAddressesByUserId(String userId) {
        log.info("Performing UserAddressService findUserAddressesByUserId");
        User user = userService.findById(userId);
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
        UserAddress existingAddress = this.findById(addressId);
        if (!existingUser.getId().equals(existingAddress.getUserId())) {
            throw new InvalidDataException(MISMATCHED_USER_IDS_MESSAGE);
        }
        if (existingAddress.getId().equals(existingUser.getDefaultAddressId())) {
            throw new InvalidDataException(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE);
        }
        userAddressRepository.deleteById(addressId);
    }

    @Override
    public UserAddress findById(String addressId) {
        log.info("Performing UserAddressService findById");
        SecurityUser securityUser = getCurrentUser();
        Optional<UserAddressEntity> addressEntityOptional = userAddressRepository.findById(addressId);
        if (addressEntityOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("User address with id [%s] is not found", addressId));
        }
        UserAddressEntity addressEntity = addressEntityOptional.get();
        if (!securityUser.getId().equals(addressEntity.getUserId())) {
            throw new InvalidDataException(MISMATCHED_USER_IDS_MESSAGE);
        }
        return DaoUtils.toData(addressEntity);
    }

    @Override
    public void deleteUserAddressesByUserId(String userId) {
        log.info("Performing UserAddressService deleteUserAddressesByUserId");
        userService.findById(userId);
        userAddressRepository.deleteUserAddressesByUserId(userId);
    }
}
