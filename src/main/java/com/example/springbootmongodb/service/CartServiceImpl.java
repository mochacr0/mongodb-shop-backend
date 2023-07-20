package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.UpdateCartItemRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.CartEntity;
import com.example.springbootmongodb.model.CartItemEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl extends DataBaseService<CartEntity> implements CartService {
    private final CartRepository cartRepository;
    private final ProductItemService itemService;
    public static final String CART_ALREADY_EXISTS_ERROR_MESSAGE = "User already has a cart";
    public static final String REQUIRED_USER_ID_ERROR_MESSAGE = "User Id should be specified";
    public static final String NON_POSITIVE_QUANTITY_ERROR_MESSAGE = "Quantity should be positive";
    public static final String MAX_QUANTITY_EXCEEDED_ERROR_MESSAGE = "The quantity you selected has exceeded the maximum for this product";

    @Autowired
    @Lazy
    private UserService userService;
    @Override
    public MongoRepository<CartEntity, String> getRepository() {
        return cartRepository;
    }

    @Override
    public CartEntity create(String userId) {
        log.info("Performing CartService create");
        userService.findById(userId);
        if (existsByUserId(userId)) {
            throw new InvalidDataException(CART_ALREADY_EXISTS_ERROR_MESSAGE);
        }
        CartEntity newCart = CartEntity
                .builder()
                .userId(userId)
                .itemMap(new HashMap<>())
                .build();
        return super.insert(newCart);
    }


    @Override
    public CartEntity findByUserId(String userId) {
        log.info("Performing CartService findByUserId");
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException(REQUIRED_USER_ID_ERROR_MESSAGE);
        }
        Optional<CartEntity> cartOpt = cartRepository.findByUserId(userId);
        return cartOpt.orElseThrow(() -> new ItemNotFoundException(String.format("Cart with userId [%s] is not found", userId)));
    }

    @Override
    public boolean existsByUserId(String userId) {
        log.info("Performing CartService existsByUserId");
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException(REQUIRED_USER_ID_ERROR_MESSAGE);
        }
        return cartRepository.existsByUserId(userId);
    }

    @Override
    public void addItem(UpdateCartItemRequest request) {
        log.info("Performing CartService addItem");
        if (request.getQuantity() <= 0) {
            throw new InvalidDataException(NON_POSITIVE_QUANTITY_ERROR_MESSAGE);
        }
        ProductItemEntity productItem;
        try {
            productItem = itemService.findById(request.getProductItemId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        CartEntity cart = getCurrentCart();
        CartItemEntity cartItem = cart.getItemMap().get(request.getProductItemId());
        if (cartItem == null) {
            //create new cart item
            cartItem = CartItemEntity
                    .builder()
                    .productItem(productItem)
                    .quantity(request.getQuantity())
                    .build();
        }
        else {
            int currentQuantity = cartItem.getQuantity();
            cartItem.setQuantity(currentQuantity + request.getQuantity());
        }
        if (cartItem.getQuantity() > productItem.getQuantity()) {
            throw new InvalidDataException(MAX_QUANTITY_EXCEEDED_ERROR_MESSAGE);
        }
        cart.getItemMap().put(request.getProductItemId(), cartItem);
        super.save(cart);
    }

    @Override
    public CartItemEntity updateItem(UpdateCartItemRequest request) {
        log.info("Performing CartService updateItem");
        if (request.getQuantity() <= 0) {
            throw new InvalidDataException(NON_POSITIVE_QUANTITY_ERROR_MESSAGE);
        }
        try {
            ProductItemEntity productItem = itemService.findById(request.getProductItemId());
            if (request.getQuantity() > productItem.getQuantity()) {
                throw new InvalidDataException(MAX_QUANTITY_EXCEEDED_ERROR_MESSAGE);
            }
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        CartEntity cart = getCurrentCart();
        CartItemEntity cartItem = cart.getItemMap().get(request.getProductItemId());
        if (cartItem != null) {
            cartItem.setQuantity(request.getQuantity());
        }
        cart.getItemMap().put(request.getProductItemId(), cartItem);
        super.save(cart);
        return cartItem;
    }

    @Override
    public void removeItem(String productItemId) {
        log.info("Performing CartService removeItem");
        if (StringUtils.isEmpty(productItemId)) {
            log.info("Product item Id should be specified");
        }
        CartEntity cart = getCurrentCart();
        cart.getItemMap().remove(productItemId);
        super.save(cart);
    }

    @Override
    public CartEntity bulkRemoveItems(List<String> productItemIds) {
        log.info("Performing CartService bulkRemoveItems");
        CartEntity cart = getCurrentCart();
        for (String productItemId : productItemIds) {
            if (StringUtils.isNotEmpty(productItemId)) {
                cart.getItemMap().remove(productItemId);
            }
        }
        return super.save(cart);
    }

    @Override
    public CartEntity getCurrentCart() {
        log.info("Performing CartService getCurrentCart");
        User user = getCurrentUser();
        return findByUserId(user.getId());
    }

    @Override
    public void deleteByUserId(String userId) {
        log.info("Performing CartService deleteByUserId");
        try {
            userService.findById(userId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        cartRepository.deleteByUserId(userId);
    }
}
