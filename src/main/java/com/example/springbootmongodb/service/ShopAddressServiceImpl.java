package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ShopAddressRequest;
import com.example.springbootmongodb.common.data.mapper.ShopAddressMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ShopAddressEntity;
import com.example.springbootmongodb.repository.ShopAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopAddressServiceImpl extends DataBaseService<ShopAddressEntity> implements ShopAddressService {
    private final ShopAddressRepository shopAddressRepository;
    private final ShopAddressMapper shopAddressMapper;
    private final int SHOP_ADDRESS_LIMIT = 10;
    public static final String DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE = "This address's default label is unchangeable. Set another default address first";

    @Override
    public MongoRepository<ShopAddressEntity, String> getRepository() {
        return this.shopAddressRepository;
    }

    @Override
    public ShopAddressEntity create(ShopAddressRequest request) {
        log.info("Performing ShopAddressService create");
        if (shopAddressRepository.count() > SHOP_ADDRESS_LIMIT) {
            throw new InvalidDataException(String.format("Only %d addresses are allowed per user.", SHOP_ADDRESS_LIMIT));
        }
        request.setId(null);
        ShopAddressEntity newAddress = shopAddressMapper.toEntity(request);
        if (request.isDefault()) {
            Optional<ShopAddressEntity> defaultAddressOpt = shopAddressRepository.findDefaultShopAddress();
            if (defaultAddressOpt.isPresent()) {
                ShopAddressEntity defaultAddress = defaultAddressOpt.get();
                defaultAddress.setDefault(false);
                super.save(defaultAddress);
            }
        }
        return super.insert(newAddress);
    }

    @Override
    public ShopAddressEntity update(String id, ShopAddressRequest request) {
        log.info("Performing ShopAddressService create");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException("Shop address Id should be specified");
        }
        ShopAddressEntity existingAddress;
        try {
            existingAddress = findById(id);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        if (existingAddress.isDefault() && !request.isDefault()) {
            throw new InvalidDataException(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE);
        }
        if (request.isDefault()) {
            Optional<ShopAddressEntity> defaultAddressOpt = shopAddressRepository.findDefaultShopAddress();
            if (defaultAddressOpt.isPresent()) {
                ShopAddressEntity defaultAddress = defaultAddressOpt.get();
                if (!defaultAddress.getId().equals(request.getId())) {
                    defaultAddress.setDefault(false);
                    super.save(defaultAddress);
                }
            }
        }
        shopAddressMapper.updateFields(existingAddress, request);
        return super.save(existingAddress);
    }

    @Override
    public ShopAddressEntity findById(String id) {
        log.info("Performing ShopAddressService findById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException("Shop address Id should be specified");
        }
        return shopAddressRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(String.format("Shop address with id [%s] is not found", id)));
    }

    @Override
    public ShopAddressEntity findDefaultAddress() {
        log.info("Performing ShopAddressService findDefaultAddress");
        return shopAddressRepository.findDefaultShopAddress().orElseThrow(() -> new ItemNotFoundException("There is no default shop address at this time"));
    }

    @Override
    public List<ShopAddressEntity> findShopAddresses() {
        log.info("Performing ShopAddressService findShopAddresses");
        return shopAddressRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        log.info("Performing ShopAddressService deleteById");
        ShopAddressEntity existingAddress = findById(id);
        if (existingAddress.isDefault()) {
            throw new InvalidDataException(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE);
        }
        shopAddressRepository.deleteById(id);
    }
}
