package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.Cart;
import com.example.springbootmongodb.model.CartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMapper {
    private final CartItemMapper cartItemMapper;
    public Cart fromEntity(CartEntity entity) {
        return Cart
                .builder()
                .userId(entity.getUserId())
                .items(entity.getItemMap().values().stream().map(cartItemMapper::fromEntity).toList())
                .build();
    }
}
