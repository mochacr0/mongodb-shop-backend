package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@Tag(name = "User Address")
@Slf4j
public class UserAddressController {
    @Autowired
    private UserAddressService userAddressService;

    @PostMapping(value = USERS_CREATE_ADDRESSES_ROUTE)
    UserAddress createAddress(@PathVariable(name = "userId") String userId,
                              @RequestBody UserAddress userAddress) {
        return userAddressService.create(userAddress);
    }

    @Operation(security = {@SecurityRequirement(name = "Bearer Auth")})
    @GetMapping(value = USERS_GET_CURRENT_USER_ADDRESSES_ROUTE)
    List<UserAddress> getAddressByUserId() {
        return userAddressService.findCurrentUserAddresses();
    }


    @GetMapping(value = USERS_GET_ADDRESS_BY_ID_ROUTE)
    UserAddress getAddressById(@PathVariable(name = "addressId") String addressId) {
        return userAddressService.findById(addressId);
    }

    @PutMapping(value = USERS_UPDATE_ADDRESSES_ROUTE)
    UserAddress updateAddress(@PathVariable(name = "userId") String userId,
                                  @PathVariable(name = "userAddressId") String addressId,
                                  @RequestBody UserAddress userAddress) {
        return userAddressService.save(addressId, userAddress);
    }

    @DeleteMapping(value = USERS_DELETE_ADDRESSES_ROUTE)
    void deleteAddressById(@PathVariable(name = "userId") String userId,
                           @PathVariable(name = "addressId") String addressId) {
        userAddressService.deleteById(addressId);
    }
}
