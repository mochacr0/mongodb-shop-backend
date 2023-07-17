package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.mapper.UserAddressMapper;
import com.example.springbootmongodb.common.utils.DaoUtils;
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
    private final UserAddressMapper mapper;

    @Operation(summary = "Tạo địa chỉ mới cho user hiện đăng đăng nhập")
    @PostMapping(value = USERS_CREATE_ADDRESSES_ROUTE)
    UserAddress createAddress(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                              @RequestBody UserAddress address) {
        return mapper.toUserAddress(userAddressService.create(address));
    }

    @Operation(summary = "Tìm tất cả địa chỉ của user hiện đang đăng nhập",
               security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    @GetMapping(value = USERS_GET_CURRENT_USER_ADDRESSES_ROUTE)
    List<UserAddress> getAddressesByUserId() {
        return mapper.toUserAddressList(userAddressService.findCurrentUserAddresses());
    }


    @Operation(summary = "Tìm một địa chỉ bất kỳ của user hiện đang đăng nhập")
    @GetMapping(value = USERS_GET_ADDRESS_BY_ID_ROUTE)
    UserAddress getAddressById(@Parameter(description = "Id của địa chỉ muốn tìm", required = true)
                               @PathVariable(name = "addressId") String addressId) {
        return mapper.toUserAddress(userAddressService.findById(addressId));
    }

    @Operation(summary = "Cập nhật một địa chỉ bất kỳ của user hiện đang đăng nhập")
    @PutMapping(value = USERS_UPDATE_ADDRESS_ROUTE)
    UserAddress updateAddress(@Parameter(description = "Id của địa chỉ muốn update")
                              @PathVariable(name = "addressId") String addressId,
                              @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                              @RequestBody UserAddress address) {
        return mapper.toUserAddress(userAddressService.save(addressId, address));
    }

    @Operation(summary = "Xóa một địa chỉ bất kỳ của user hiện đang đăng nhập")
    @DeleteMapping(value = USERS_DELETE_ADDRESS_BY_ID_ROUTE)
    void deleteAddressById(@Parameter(description = "Id của địa chỉ muốn xóa")
                           @PathVariable(name = "addressId") String addressId) {
        userAddressService.deleteById(addressId);
    }
}
