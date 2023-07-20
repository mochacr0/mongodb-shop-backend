package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Cart;
import com.example.springbootmongodb.common.data.CartItem;
import com.example.springbootmongodb.common.data.UpdateCartItemRequest;
import com.example.springbootmongodb.common.data.mapper.CartItemMapper;
import com.example.springbootmongodb.common.data.mapper.CartMapper;
import com.example.springbootmongodb.model.CartItemEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {
    private final CartService cartService;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @GetMapping(value = CART_GET_CURRENT_CART_ROUTE)
    @Operation(summary = "Truy xuất giỏ hàng của user hiện tại")
    Cart getCurrentCart() {
        return cartMapper.fromEntity(cartService.getCurrentCart());
    }

    @PostMapping(value = CART_ADD_ITEM_ROUTE)
    @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
    void addItem(@RequestBody UpdateCartItemRequest request) {
        cartService.addItem(request);
    }

    @PutMapping(value = CART_UPDATE_ITEM_ROUTE)
    @Operation(summary = "Thay đổi số lượng sản phẩm trong giỏ")
    CartItem updateItem(@RequestBody UpdateCartItemRequest request) {
        return cartItemMapper.fromEntity(cartService.updateItem(request));
    }

    @PutMapping(value = CART_REMOVE_ITEMS_ROUTE)
    @Operation(summary = "Xóa một hoặc nhiều sản phẩm khỏi giỏ hàng")
    Cart bulkRemoveItems(@RequestBody List<String> productItemIds) {
        return cartMapper.fromEntity(cartService.bulkRemoveItems(productItemIds));
    }
}
