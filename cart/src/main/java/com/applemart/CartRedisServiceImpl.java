package com.applemart;

import com.applemart.clients.ProductClient;
import com.applemart.exception.ResourceNotFoundException;
import com.applemart.productItem.ProductItemDTO;
import com.applemart.redis.BaseRedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartRedisServiceImpl extends BaseRedisServiceImpl implements CartRedisService {

    private static final String CART_KEY_PREFIX = "cart:user-";
    private static final String CART_PRODUCT_ITEM_FIELD_PREFIX = "product_item:";

    private final ProductClient productClient;

    @Autowired
    public CartRedisServiceImpl(RedisTemplate<String, Object> redisTemplate, ProductClient productClient) {
        super(redisTemplate);
        this.productClient = productClient;
    }

    private boolean isAuthorized(String userId) {
        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        return userId.equals(authenticatedUserId);
    }

    public List<ProductItemDTO> getProductsFromCartByUserId(String userId) {

        if (!isAuthorized(userId)) {
            throw new AccessDeniedException("You are not authorized to get product items from this cart");
        }

        String key = CART_KEY_PREFIX + userId;

        Map<String, Object> products = getField(key);

        List<ProductItemDTO> productItemDTOList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : products.entrySet()) {
            String[] arrKey = entry.getKey().split(":");

            ProductItemDTO productItem = productClient.getProductItemById(arrKey[1]);

            if (productItem != null) {
                int quantity = (int) hashGet(key, entry.getKey());
                productItem.setQuantity(quantity);
                productItemDTOList.add(productItem);
            }
        }

        productItemDTOList.sort(Comparator.comparing(ProductItemDTO::getId));
        return productItemDTOList;
    }

    public void addProductToCart(String userId, CartItemRequest cartItemRequest) {

        if (!isAuthorized(userId)) {
            throw new AccessDeniedException("You are not authorized to modify this cart");
        }

        String key = CART_KEY_PREFIX + userId;

        String fieldKey;

        Integer quantity;

        if (Objects.nonNull(cartItemRequest.getItems())) {
            for (Map.Entry<String, Integer> entry : cartItemRequest.getItems().entrySet()) {
                fieldKey = CART_PRODUCT_ITEM_FIELD_PREFIX + entry.getKey();

                if (hashExist(key, fieldKey)) {
                    quantity = (Integer) this.hashGet(key, fieldKey) + entry.getValue();
                } else {
                    quantity = entry.getValue();
                }
                this.hashSet(key, fieldKey, quantity);
            }
        }
    }

    @Override
    public void updateProductInCart(String userId, CartItemRequest cartItemRequest) {

        if (!isAuthorized(userId)) {
            throw new AccessDeniedException("You are not authorized to modify this cart");
        }
        String key = CART_KEY_PREFIX + userId;

        String fieldKey;

        Integer quantity;

        if (Objects.nonNull(cartItemRequest.getItems())) {
            for (Map.Entry<String, Integer> entry : cartItemRequest.getItems().entrySet()) {
                fieldKey = CART_PRODUCT_ITEM_FIELD_PREFIX + entry.getKey();
                quantity = entry.getValue();
                if (quantity <= 0) {
                    delete(key, fieldKey);
                } else {
                    hashSet(key, fieldKey, quantity);
                }
            }
        }
    }

    @Override
    public void deleteProductsInCart(String userId, CartItemDeletionRequest cartItemRequest) {

        if (!isAuthorized(userId)) {
            throw new AccessDeniedException("You are not authorized to modify this cart");
        }

        String key = CART_KEY_PREFIX + userId;

        String fieldKey;

        if (Objects.nonNull(cartItemRequest.getProductItemIds())) {
            for (String productItemId : cartItemRequest.getProductItemIds()) {
                fieldKey = CART_PRODUCT_ITEM_FIELD_PREFIX + productItemId;

                if (!this.hashExist(key, fieldKey)) {
                    throw new ResourceNotFoundException("Key [%s] with keyField [%s] not found".formatted(key, fieldKey));
                }
                delete(key, fieldKey);
            }
        }
    }

    @Override
    public void deleteAllProductsInCart(String userId) {

        if (!isAuthorized(userId)) {
            throw new AccessDeniedException("You are not authorized to modify this cart");
        }
        delete(CART_KEY_PREFIX + userId);
    }
}
