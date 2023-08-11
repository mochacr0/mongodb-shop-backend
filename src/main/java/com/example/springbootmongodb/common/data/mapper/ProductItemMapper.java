package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.ProductItem;
import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.common.data.payment.momo.MomoPayWithMethodItem;
import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductItemMapper {
    @Autowired
    @Lazy
    private ProductMapper productMapper;
    @Autowired
    @Lazy
    private VariationOptionMapper optionMapper;
    public ProductItemEntity toEntity(ProductItemRequest request) {
        return ProductItemEntity
                .builder()
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .options(new ArrayList<>())
                .weight(request.getWeight())
                .build();
    }

    public ProductItem fromEntity(ProductItemEntity entity) {
        return ProductItem
                .builder()
                .id(entity.getId())
                .variationDescription(entity.getVariationDescription())
                .imageUrl(entity.getImageUrl())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .product(productMapper.fromEntityToSimplification(entity.getProduct()))
//                .options(DaoUtils.toListData(entity.getOptions(), optionMapper::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public MomoPayWithMethodItem fromOrderItemToMomoItem(OrderItem orderItem) {
        return MomoPayWithMethodItem
                .builder()
                .id(orderItem.getProductItemId())
                .name(orderItem.getProductName())
                .imageUrl(orderItem.getImageUrl())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getPrice() * orderItem.getQuantity())
                .build();
    }

    public void updateFields(ProductItemEntity entity, ProductItemRequest request) {
        List<String> variations = new ArrayList<>();
        for (VariationOptionEntity option : entity.getOptions()) {
            variations.add(String.format("%s:%s", option.getVariation().getName(), option.getName()));
        }
        String variationDescription = String.join(", ", variations);
        String variationIndex = request.getVariationIndex().stream().map(String::valueOf).collect(Collectors.joining(","));
        entity.setQuantity(request.getQuantity());
        entity.setPrice(request.getPrice());
        entity.setWeight(request.getWeight());
        entity.setVariationDescription(variationDescription);
        entity.setVariationIndex(variationIndex);
        entity.setVariationIndex(variationIndex);
    }
}
