package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.CartItem;
import com.example.springbootmongodb.model.CartItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemMapper {
    private final ProductItemMapper itemMapper;
    public CartItem fromEntity(CartItemEntity entity) {
        return CartItem
                .builder()
                .productItem(itemMapper.fromEntity(entity.getProductItem()))
                .quantity(entity.getQuantity())
                .build();
    }
}
