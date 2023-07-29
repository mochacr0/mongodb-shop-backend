package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.ShopAddressRequest;
import com.example.springbootmongodb.common.data.mapper.ShopAddressMapper;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.service.ShopAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Shop Address")
public class ShopAddressController {
    private final ShopAddressService shopAddressService;
    private final ShopAddressMapper shopAddressMapper;

    @PostMapping(value = SHOP_ADDRESS_CREATE_ADDRESS_ROUTE)
    @Operation(summary = "Tạo địa chỉ cửa hàng mới")
    ShopAddress create(@RequestBody ShopAddressRequest request) {
        return shopAddressMapper.fromEntity(shopAddressService.create(request));
    }

    @PutMapping(value = SHOP_ADDRESS_UPDATE_ADDRESS_BY_ID_ROUTE)
    @Operation(summary = "Cập nhập địa chi cửa hàng")
    ShopAddress update(@PathVariable String shopAddressId,
                       @RequestBody ShopAddressRequest request) {
        return shopAddressMapper.fromEntity(shopAddressService.update(shopAddressId, request));
    }

    @GetMapping(value = SHOP_ADDRESS_GET_ADDRESS_BY_ID_ROUTE)
    @Operation(summary = "Truy xuất địa chỉ theo id")
    ShopAddress getById(@PathVariable String shopAddressId) {
        return shopAddressMapper.fromEntity(shopAddressService.findById(shopAddressId));
    }

    @GetMapping(value = SHOP_ADDRESS_GET_ADDRESSES_ROUTE)
    @Operation(summary = "Truy xuất tất cả địa chỉ của shop")
    List<ShopAddress> getShopAddresses() {
        return DaoUtils.toListData(shopAddressService.findShopAddresses(), shopAddressMapper::fromEntity);
    }

    @DeleteMapping(value = SHOP_ADDRESS_DELETE_ADDRESS_BY_ID_ROUTE)
    @Operation(summary = "Xóa địa chỉ theo Id")
    void deleteById(@PathVariable String shopAddressId) {
        shopAddressService.deleteById(shopAddressId);
    }
}
