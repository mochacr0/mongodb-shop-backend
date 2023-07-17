package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.ProductItem;
import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
                .build();
    }

    public ProductItem fromEntity(ProductItemEntity entity) {
        String imageUrl = entity.getProduct().getImageUrl();
        List<String> variations = new ArrayList<>();
        for (VariationOptionEntity option : entity.getOptions()) {
            if (StringUtils.isNotEmpty(option.getImageUrl())) {
                imageUrl = option.getImageUrl();
            }
            variations.add(String.format("%s:%s", option.getVariation().getName(), option.getName()));
        }
        String variationDescription = String.join(", ", variations);
        return ProductItem
                .builder()
                .id(entity.getId())
                .variationDescription(variationDescription)
                .imageUrl(imageUrl)
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .product(productMapper.fromEntityToSimplification(entity.getProduct()))
                .options(DaoUtils.toListData(entity.getOptions(), optionMapper::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateFields(ProductItemEntity entity, ProductItemRequest request) {
        entity.setQuantity(request.getQuantity());
        entity.setPrice(request.getPrice());
    }

}
