package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.security.core.parameters.P;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.service.ProductItemServiceImpl.*;
import static com.example.springbootmongodb.service.ProductServiceImpl.DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE;
import static com.example.springbootmongodb.service.ProductVariationServiceImpl.DUPLICATED_VARIANT_NAME_ERROR_MESSAGE;
import static com.example.springbootmongodb.service.ProductVariationServiceImpl.REQUIRED_MINIMUM_VARIATIONS_ERROR_MESSAGE;
import static com.example.springbootmongodb.service.VariationOptionServiceImpl.DUPLICATED_OPTION_NAME_ERROR_MESSAGE;
import static com.example.springbootmongodb.service.VariationOptionServiceImpl.REQUIRED_MINIMUM_OPTIONS_ERROR_MESSAGE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerTest extends AbstractControllerTest {
    private User user;
    @BeforeAll
    void setUp() throws Exception {
        user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
        activateUser(user.getId());
        login(user.getName(), DEFAULT_PASSWORD);
    }

    @AfterAll
    void tearDown() throws Exception {
        if (user != null && StringUtils.isNotEmpty(user.getId())) {
            User existingUser = performGet(USERS_GET_USER_BY_ID_ROUTE, User.class, user.getId());
            if (existingUser != null) {
                deleteUser(user.getId());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateProductTest {
        @Test
        void testCreateProductWithValidBody() throws Exception {
            ProductRequest productRequest = createProductRequest();
            List<ProductVariationRequest> variationRequests = new ArrayList<>();
            variationRequests.add(createVariationRequest(2));
            variationRequests.add(createVariationRequest(1));
            List<ProductItemRequest> itemRequests = new ArrayList<>();
            itemRequests.add(createItemRequest(0,0));
            itemRequests.add(createItemRequest(1,0));
            productRequest.setVariations(variationRequests);
            productRequest.setItems(itemRequests);
            Product createdProduct = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, productRequest);
            assertProduct(productRequest, createdProduct);
        }

        @Test
        void testCreateProductWithDuplicatedName() throws Exception {
            Product createdProduct1 = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
            ProductRequest productRequest2 = createProductRequestSample();
            productRequest2.setName(createdProduct1.getName());
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest2)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testCreateProductWithInsufficientItems() throws Exception {
            ProductRequest productRequest = createProductRequest();
            List<ProductVariationRequest> variationRequests = new ArrayList<>();
            variationRequests.add(createVariationRequest(2));
            variationRequests.add(createVariationRequest(1));
            List<ProductItemRequest> itemRequests = new ArrayList<>();
            itemRequests.add(createItemRequest(0));
            productRequest.setVariations(variationRequests);
            productRequest.setItems(itemRequests);
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE)));
        }

        @Test
        void testCreateProductWithDuplicatedOptionNames() throws Exception {
            ProductRequest productRequest = createProductRequest();
            //create variation with duplicate option names
            ProductVariationRequest variationRequest = createVariationRequest(2);
            String duplicatedOptionName = variationRequest.getOptions().get(0).getName();
            variationRequest.getOptions().get(1).setName(duplicatedOptionName);
            productRequest.setVariations(Collections.singletonList(variationRequest));
            productRequest.setItems(Collections.singletonList(createItemRequest(0)));
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE,  productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_OPTION_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testCreateProductWithDuplicatedVariationNames() throws Exception {
            ProductRequest productRequest = createProductRequest();
            List<ProductVariationRequest> variationRequests = new ArrayList<>();
            List<ProductItemRequest> itemRequests = new ArrayList<>();

            //create variations with duplicated names
            ProductVariationRequest variationRequest = createVariationRequest(2);
            ProductVariationRequest nameDuplicatedVariationRequest = createVariationRequest(1);
            nameDuplicatedVariationRequest.setName(variationRequest.getName());
            variationRequests.add(variationRequest);
            variationRequests.add(nameDuplicatedVariationRequest);

            //create items
            itemRequests.add(createItemRequest(0,0));
            itemRequests.add(createItemRequest(1,0));
            productRequest.setVariations(variationRequests);
            productRequest.setItems(itemRequests);
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE,  productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_VARIANT_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testCreateProductWithNoVariations() throws Exception {
            ProductRequest productRequest = createProductRequest();
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_MINIMUM_VARIATIONS_ERROR_MESSAGE)));
        }

        @Test
        void testCreateProductWithNoOptionsVariation() throws Exception {
            ProductRequest productRequest = createProductRequest();
            productRequest.setVariations(Collections.singletonList(createVariationRequest()));
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_MINIMUM_OPTIONS_ERROR_MESSAGE)));
        }

        @Test
        void testCreateProductWithInvalidItems() throws Exception {
            ProductRequest productRequest = createProductRequestSample();
            for (ProductItemRequest itemRequest : productRequest.getItems()) {
                itemRequest.setSku(-1);
            }
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(NON_POSITIVE_SKU_ERROR_MESSAGE)));
            productRequest = createProductRequestSample();
            for (ProductItemRequest itemRequest : productRequest.getItems()) {
                itemRequest.setPrice(-1);
            }
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(MINIMUM_PRICE_VIOLATION_ERROR_MESSAGE)));
            productRequest = createProductRequestSample();
            for (ProductItemRequest itemRequest : productRequest.getItems()) {
                itemRequest.setPrice(0);
            }
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(MINIMUM_PRICE_VIOLATION_ERROR_MESSAGE)));
        }
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
        performDelete(PRODUCT_DELETE_PRODUCT_BY_ID_ROUTE, product.getId()).andExpect(status().isOk());
        performGet(PRODUCT_GET_PRODUCT_BY_ID_ROUTE, product.getId()).andExpect(status().isNotFound());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateProductTest {
        private Product product;
        @BeforeEach
        void setUp() throws Exception {
            product = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
        }

        @AfterEach
        void tearDown() throws Exception {
            deleteProduct(product.getId());
        }

        @Test
        void testUpdateNonExistentProduct() throws Exception {
            deleteProduct(product.getId());
            product.setName(generateRandomString());
            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, product, product.getId()).andExpect(status().isNotFound());
        }

        @Test
        void testUpdateProductWithDuplicatedName() throws Exception {
            Product newProduct = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
            newProduct.setName(product.getName());
            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, newProduct, newProduct.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_PRODUCT_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateProductWithNameDuplicatedVariations() throws Exception {
            ProductRequest productRequest = fromProductToRequest(product);
            ProductVariationRequest newVariationRequest = createVariationRequest(1);
            newVariationRequest.setName(productRequest.getVariations().get(0).getName());
            productRequest.getVariations().add(newVariationRequest);
            ProductItemRequest newItemRequest = createItemRequest(0,0);
            productRequest.setItems(Collections.singletonList(newItemRequest));
            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, productRequest, productRequest.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_VARIANT_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateProductWithNameDuplicatedOptions() throws Exception {
            // Create the product request
            ProductRequest productRequest = fromProductToRequest(product);

            // Create a new option request with a duplicated name
            String duplicatedOptionName = productRequest.getVariations().get(0).getOptions().get(0).getName();
            VariationOptionRequest newOptionRequest = createOptionRequest();
            newOptionRequest.setName(duplicatedOptionName);

            // Add the new option request to the variations
            productRequest.getVariations().get(0).getOptions().add(newOptionRequest);

            // Create the item requests
            ProductItemRequest newItemRequest1 = createItemRequest(0);
            ProductItemRequest newItemRequest2 = createItemRequest(1);

            // Add the item requests to the product request
            productRequest.setItems(Arrays.asList(newItemRequest1, newItemRequest2));

            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, productRequest, productRequest.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_OPTION_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateProductWithNoVariations() throws Exception {
            ProductRequest productRequest = fromProductToRequest(product);
            productRequest.setVariations(null);
            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, productRequest, productRequest.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_MINIMUM_VARIATIONS_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateProductWithNoOptions() throws Exception {
            ProductRequest productRequest = fromProductToRequest(product);
            productRequest.getVariations().get(0).setOptions(null);
            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, productRequest, productRequest.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_MINIMUM_OPTIONS_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateProductWithInsufficientItems() throws Exception {
            ProductRequest productRequest = new ProductRequest();
            //create new valid product
            productRequest.setName(generateRandomString());
            List<ProductVariationRequest> variationRequests = new ArrayList<>();
            variationRequests.add(createVariationRequest(2));
            variationRequests.add(createVariationRequest(1));
            List<ProductItemRequest> itemRequests = new ArrayList<>();
            itemRequests.add(createItemRequest(0,0));
            itemRequests.add(createItemRequest(1,0));
            productRequest.setVariations(variationRequests);
            productRequest.setItems(itemRequests);
            Product createdProduct = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, productRequest);
            productRequest = fromProductToRequest(createdProduct);
            //remove the last item;
            productRequest.setItems(productRequest.getItems().subList(0, productRequest.getItems().size() - 1));
            performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, productRequest, productRequest.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(PRODUCT_MISSING_ITEMS_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateProductWithValidBody() throws Exception {
            ProductRequest productRequest = new ProductRequest();
            //create new valid product
            productRequest.setName(generateRandomString());
            List<ProductVariationRequest> variationRequests = new ArrayList<>();
            variationRequests.add(createVariationRequest(2));
            variationRequests.add(createVariationRequest(1));
            List<ProductItemRequest> itemRequests = new ArrayList<>();
            itemRequests.add(createItemRequest(0,0));
            itemRequests.add(createItemRequest(1,0));
            productRequest.setVariations(variationRequests);
            productRequest.setItems(itemRequests);
            Product createdProduct = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, productRequest);
            productRequest = fromProductToRequest(createdProduct);
            //update sku of the first item
            productRequest.getItems().get(0).setSku(2000);
            Product updatedProduct = performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, Product.class, productRequest, productRequest.getId());
            assertProduct(productRequest, updatedProduct);
            deleteProduct(updatedProduct.getId());
        }

        @Test
        void testUpdateProductWithNewVariationAndIgnoreOldItems() throws Exception {
            ProductItem oldItem = product.getItemMap().get("0");
            ProductRequest productRequest = fromProductToRequest(product);
            ProductVariationRequest variationRequest = productRequest.getVariations().get(0);
            variationRequest.setId(null);
            //save old item
            Product updatedProduct = performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, Product.class, productRequest, productRequest.getId());
            assertProduct(productRequest, updatedProduct);
            ProductItem newItem = updatedProduct.getItemMap().get("0");
            Assertions.assertNotEquals(oldItem.getId(), newItem.getId());
            oldItem = newItem;
            List<ProductVariationRequest> variationRequests = productRequest.getVariations();
            variationRequests.add(createVariationRequest(1));
            variationRequests.add(createVariationRequest(2));
            List<ProductItemRequest> itemRequests = productRequest.getItems();
            itemRequests.get(0).setVariationIndex(Arrays.asList(0,0,0));
            itemRequests.get(0).setSku(99999);
            itemRequests.add(createItemRequest(0,0,1));
            updatedProduct = performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, Product.class, productRequest, productRequest.getId());
            assertProduct(productRequest, updatedProduct);
            newItem = updatedProduct.getItemMap().get("0,0,0");
            Assertions.assertEquals(oldItem.getPrice(), newItem.getPrice());
            Assertions.assertEquals(99999, newItem.getSku());
            Assertions.assertNotEquals(oldItem.getId(), newItem.getId());
        }

        @Test
        void testUpdateProductWithNewOptionAndIgnoreOldItems() throws Exception {
            ProductItem oldItem = product.getItemMap().get("0");
            ProductRequest productRequest = fromProductToRequest(product);
            ProductVariationRequest variationRequest = productRequest.getVariations().get(0);
            variationRequest.getOptions().remove(0);
            variationRequest.getOptions().add(createOptionRequest());
            //save old item
            Product updatedProduct = performPut(PRODUCT_UPDATE_PRODUCT_ROUTE, Product.class, productRequest, productRequest.getId());
            assertProduct(productRequest, updatedProduct);
            ProductItem newItem = updatedProduct.getItemMap().get("0");
            Assertions.assertNotEquals(oldItem.getId(), newItem.getId());
        }
    }

    @Test
    void testFindProducts() throws Exception {
        List<ProductRequest> productRequests = new ArrayList<>();
        int totalRequests = 5;
        for (int i = 0; i < totalRequests; i++) {
            ProductRequest productRequest = createProductRequestSample();
            performPost(PRODUCT_CREATE_PRODUCT_ROUTE, productRequest);
            productRequests.add(productRequest);
        }
        PageData<Product> pageData;
        List<Product> createdProducts = new ArrayList<>();
        int currentPage = 0;
        do {
            pageData = performGetWithReferencedType(PRODUCT_GET_PRODUCTS_ROUTE +
                            "?page={page}&pageSize={pageSize}&sortDirection={sortDirection}&sortProperty={sortProperty}",
                    new TypeReference<>(){},
                    currentPage,
                    3,
                    "desc",
                    "createdAt");
            createdProducts.addAll(pageData.getData());
            if (pageData.hasNext()) {
                currentPage++;
            }
        } while (pageData.hasNext() && createdProducts.size() < totalRequests);
        createdProducts = createdProducts.subList(0, totalRequests);
        for (Product product : createdProducts) {
            deleteProduct(product.getId());
        }
        createdProducts.sort(new ProductComparator<>());
        boolean areListsTheSame = true;
        for (int i = 0; i < productRequests.size(); i++) {
            if (!productRequests.get(i).getName().equals(createdProducts.get(i).getName())) {
                areListsTheSame = false;
                break;
            }
        }
        Assertions.assertTrue(areListsTheSame, "The expected list and the actual list are not equal");
    }

    //create
        //invalid body
            //non-existent category

    //update
        //invalid
            //non-existent category
        //valid

    private ProductRequest createProductRequest() {
        ProductRequest product = ProductRequest
                .builder()
                .name(generateRandomString())
                .variations(new ArrayList<>())
                .items(new ArrayList<>())
                .build();

        return product;
    }

    private ProductRequest createProductRequestSample() {
        ProductRequest productRequest = createProductRequest();
        productRequest.setVariations(Collections.singletonList(createVariationRequest(1)));
        productRequest.setItems(Collections.singletonList(createItemRequest(0)));
        return productRequest;
    }

    private ProductVariationRequest createVariationRequest() {
        return ProductVariationRequest
                .builder()
                .name(generateRandomString())
                .options(new ArrayList<>())
                .build();
    }

    private ProductVariationRequest createVariationRequest(int totalOptions) {
        List<VariationOptionRequest> options = new ArrayList<>();
        for (int i = 0; i < totalOptions; i++) {
            options.add(createOptionRequest());
        }
        return ProductVariationRequest
                .builder()
                .name(generateRandomString())
                .options(options)
                .build();
    }

    private VariationOptionRequest createOptionRequest() {
        VariationOptionRequest option = VariationOptionRequest
                .builder()
                .name(generateRandomString())
                .build();
        return option;
    }

    private ProductItemRequest createItemRequest(Integer requiredIndex, Integer...additionalIndexes) {
        List<Integer> indexes = new ArrayList<>();
        indexes.add(requiredIndex);
        indexes.addAll(List.of(additionalIndexes));
        return ProductItemRequest
                .builder()
                .sku(100)
                .price(100f)
                .variationIndex(indexes)
                .build();
    }

    private void deleteProduct(String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            Product product = performGet(PRODUCT_GET_PRODUCT_BY_ID_ROUTE, Product.class, id);
            if (product != null) {
                performDelete(PRODUCT_DELETE_PRODUCT_BY_ID_ROUTE, id);
            }
        }
    }

    private ProductRequest fromProductToRequest(Product product) {
        return ProductRequest
                .builder()
                .id(product.getId())
                .name(product.getName())
                .variations(product.getVariations().stream().map(this::fromVariationToRequest).collect(Collectors.toList()))
                .items(fromItemMapToItemRequests(product.getItemMap()))
                .build();
    }

    private List<ProductItemRequest> fromItemMapToItemRequests(Map<String, ProductItem> itemMap) {
        List<ProductItemRequest> itemRequests = new ArrayList<>();
        for (Map.Entry<String, ProductItem> entry : itemMap.entrySet()) {
            ProductItemRequest itemRequest = fromItemToRequest(entry.getValue());
            itemRequest.setVariationIndex(Arrays.stream(entry.getKey().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            itemRequests.add(itemRequest);
        }
        return itemRequests;
    }

    private ProductItemRequest fromItemToRequest(ProductItem item) {
        return ProductItemRequest
                .builder()
                .id(item.getId())
                .sku(item.getSku())
                .price(item.getPrice())
                .build();
    }

    private ProductVariationRequest fromVariationToRequest(ProductVariation variation) {
        return ProductVariationRequest
                .builder()
                .id(variation.getId())
                .productId(variation.getProductId())
                .name(variation.getName())
                .options(variation.getOptions().stream().map(this::fromOptionToRequest).collect(Collectors.toList()))
                .build();
    }

    private VariationOptionRequest fromOptionToRequest(VariationOption option) {
        return VariationOptionRequest
                .builder()
                .id(option.getId())
                .name(option.getName())
                .build();
    }

    private void assertProduct(ProductRequest productRequest, Product createdProduct) {
        Assertions.assertEquals(productRequest.getName(), createdProduct.getName());
        Assertions.assertEquals(productRequest.getVariations().size(), createdProduct.getVariations().size());
        for (int i = 0; i < productRequest.getVariations().size(); i++) {
            assertVariation(productRequest.getVariations().get(i), createdProduct.getVariations().get(i));
        }
        for (ProductItemRequest request : productRequest.getItems()) {
            String indexKey = request.getVariationIndex().stream().map(Object::toString).collect(Collectors.joining(","));
            Assertions.assertNotNull(createdProduct.getItemMap().get(indexKey));
            assertItem(request, createdProduct.getItemMap().get(indexKey));
        }
    }

    private void assertVariation(ProductVariationRequest variationRequest, ProductVariation createdVariation) {
        Assertions.assertEquals(variationRequest.getName(), createdVariation.getName());
        Assertions.assertEquals(variationRequest.getOptions().size(), createdVariation.getOptions().size());
        for (int i = 0; i < variationRequest.getOptions().size(); i++) {
            assertOption(variationRequest.getOptions().get(i), createdVariation.getOptions().get(i));
        }

    }

    private void assertOption(VariationOptionRequest optionRequest, VariationOption createdOption) {
        Assertions.assertEquals(optionRequest.getName(), createdOption.getName());
    }

    private void assertItem(ProductItemRequest itemRequest, ProductItem createdItem) {
        Assertions.assertEquals(itemRequest.getSku(), createdItem.getSku());
        Assertions.assertEquals(itemRequest.getPrice(), createdItem.getPrice());
    }

    public class ProductComparator<T extends Product> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        }
    }
}
