package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.mapper.UserAddressMapper;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.model.UserEntity;
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
public class UserAddressServiceImpl extends DataBaseService<UserAddressEntity> implements UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper mapper;
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
    public UserAddressEntity create(UserAddress address) {
        log.info("Performing UserAddressService create");
        UserEntity existingUser = userService.findById(getCurrentUser().getId());
        long totalAddresses = userAddressRepository.countByUserId(existingUser.getId());
        if (totalAddresses >= 5) {
            throw new InvalidDataException("Only 5 addresses are allowed per user.");
        }
        if (StringUtils.isEmpty(existingUser.getDefaultAddressId())) {
            address.setDefault(true);
        }
        address.setUserId(getCurrentUser().getId());
        UserAddressEntity createdUserAddress = super.insert(mapper.toEntity(address));
        if (address.isDefault()) {
            existingUser.setDefaultAddressId(createdUserAddress.getId());
            userService.save(existingUser);
        }
        return createdUserAddress;
    }

    @Override
    @Transactional
    public UserAddressEntity save(String addressId, UserAddress updateRequest) {
        log.info("Performing UserAddressService save");
        Optional<UserAddressEntity> existingAddressEntityOpt = userAddressRepository.findById(addressId);
        if (existingAddressEntityOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("User address with id [%s] is not found", addressId));
        }
        UserAddressEntity existingAddressEntity = existingAddressEntityOpt.get();
        UserEntity existingUser = userService.findById(getCurrentUser().getId());
        if (!existingUser.getId().equals(existingAddressEntity.getUserId())) {
            throw new InvalidDataException(MISMATCHED_USER_IDS_MESSAGE);
        }
        //if the current  address is the default address
        if (existingUser.getDefaultAddressId().equals(existingAddressEntity.getId()) && !updateRequest.isDefault()) {
            throw new InvalidDataException(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE);
        }
        mapper.updateFields(existingAddressEntity, updateRequest);
        UserAddressEntity savedAddress = super.save(existingAddressEntity);
        existingUser.setDefaultAddressId(savedAddress.getId());
        userService.save(existingUser);
        return savedAddress;
    }

    @Override
    public List<UserAddressEntity> findUserAddressesByUserId(String userId) {
        log.info("Performing UserAddressService findUserAddressesByUserId");
        return userAddressRepository.findByUserId(userId);
    }

    @Override
    public List<UserAddressEntity> findCurrentUserAddresses() {
        log.info("Performing UserAddressService findCurrentUserAddresses");
        return findUserAddressesByUserId(getCurrentUser().getId());
    }

    @Override
    public void deleteById(String addressId) {
        log.info("Performing UserAddressService deleteById");
        UserEntity existingUser = userService.findById(getCurrentUser().getId());
        UserAddressEntity existingAddress = this.findById(addressId);
        if (!existingUser.getId().equals(existingAddress.getUserId())) {
            throw new InvalidDataException(MISMATCHED_USER_IDS_MESSAGE);
        }
        if (existingAddress.getId().equals(existingUser.getDefaultAddressId())) {
            throw new InvalidDataException(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE);
        }
        userAddressRepository.deleteById(addressId);
    }

    @Override
    public UserAddressEntity findById(String addressId) {
        log.info("Performing UserAddressService findById");
        SecurityUser securityUser = getCurrentUser();
        if (StringUtils.isEmpty(addressId)) {
            throw new InvalidDataException("Address Id should be specified");
        }
        Optional<UserAddressEntity> addressEntityOptional = userAddressRepository.findById(addressId);
        if (addressEntityOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("User address with id [%s] is not found", addressId));
        }
        UserAddressEntity addressEntity = addressEntityOptional.get();
        if (!securityUser.getId().equals(addressEntity.getUserId())) {
            throw new InvalidDataException(MISMATCHED_USER_IDS_MESSAGE);
        }
        return addressEntity;
    }

    @Override
    public void deleteUserAddressesByUserId(String userId) {
        log.info("Performing UserAddressService deleteUserAddressesByUserId");
        userService.findById(userId);
        userAddressRepository.deleteUserAddressesByUserId(userId);
    }
}
