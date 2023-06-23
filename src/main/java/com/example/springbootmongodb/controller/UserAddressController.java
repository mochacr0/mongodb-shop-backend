package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@Tag(name = "User Address")
@Slf4j
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @Operation(summary = "Create a new user address")
    @PostMapping(value = USERS_CREATE_ADDRESSES_ROUTE)
    UserAddress createAddress(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                                    description = "UserAddress object containing the address details")
                              @RequestBody UserAddress address) {
        return userAddressService.create(address);
    }

    @Operation(summary = "Retrieve all addresses associated with the current user",
               security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    @GetMapping(value = USERS_GET_CURRENT_USER_ADDRESSES_ROUTE)
    List<UserAddress> getAddressByUserId() {
        return userAddressService.findCurrentUserAddresses();
    }


    @Operation(summary = "Retrieve a specific address by the provided addressId")
    @GetMapping(value = USERS_GET_ADDRESS_BY_ID_ROUTE)
    UserAddress getAddressById(@Parameter(description = "ID of the address to retrieve", required = true)
                               @PathVariable(name = "addressId") String addressId) {
        return userAddressService.findById(addressId);
    }

    @Operation(summary = "Update an existing user address by the provided addressId")
    @PutMapping(value = USERS_UPDATE_ADDRESS_ROUTE)
    UserAddress updateAddress(@Parameter(description = "ID of the address to update")
                              @PathVariable(name = "addressId") String addressId,
                              @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                                    description = "UserAddress object containing the updated address details.")
                              @RequestBody UserAddress address) {
        return userAddressService.save(addressId, address);
    }

    @Operation(summary = "Delete an existing user address by the provided addressId")
    @DeleteMapping(value = USERS_DELETE_ADDRESS_BY_ID_ROUTE)
    void deleteAddressById(@Parameter(description = "ID of the address to delete")
                           @PathVariable(name = "addressId") String addressId) {
        userAddressService.deleteById(addressId);
    }
}
