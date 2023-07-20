package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.config.UserPasswordPolicy;
import com.example.springbootmongodb.security.JwtTokenPair;
import com.example.springbootmongodb.security.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    protected SecuritySettingsConfiguration securitySettings;
    private String accessToken;
    private String refreshToken;
    private PasswordGenerator passwordGenerator;
    protected String DEFAULT_PASSWORD;

    protected final String NON_EXISTENT_ID = "64805c5bdb4a3449c81a9bed";

    protected final String DEFAULT_IMAGE_URL = "https://mochaimages.s3.ap-southeast-1.amazonaws.com/Screenshot+2023-07-12+200714.png";


    @PostConstruct
    private void setUp() {
        passwordGenerator = new PasswordGenerator();
        DEFAULT_PASSWORD = generatePassword();
    }

//    @SuppressWarnings("rawtypes")
//    private HttpMessageConverter mappingJackson2HttpMessageConverter;
//    @SuppressWarnings("rawtypes")
//    private HttpMessageConverter stringHttpMessageConverter;

//    @Autowired
//    void setConverters(List<HttpMessageConverter> converters) {
//        mappingJackson2HttpMessageConverter = converters
//                .stream()
//                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
//                .findAny()
//                .get();
//        stringHttpMessageConverter = converters
//                .stream()
//                .filter(hmc -> hmc instanceof StringHttpMessageConverter)
//                .findAny()
//                .get();
//        Assert.notNull(this.mappingJackson2HttpMessageConverter, "JSON message convert can not be null");
//    }

    public <T> T performGet(String urlTemplate, Class<T> responseClass, Object... urlVariables) throws Exception {
        return readResponse(performGet(urlTemplate, urlVariables), responseClass);
    }

    public <T> T performGetWithReferencedType(String urlTemplate, TypeReference<T> type, Object... urlVariables) throws Exception {
        return readResponse(performGet(urlTemplate, urlVariables), type);
    }

    public ResultActions performGet(String urlTemplate, Object... urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate, urlVariables);
        setJwtToken(builder);
        return mockMvc.perform(builder);
    }

    public <T, V> T performPost(String urlTemplate, Class<T> responseClass, V content, Object... urlVariables) throws Exception {
        return readResponse(performPost(urlTemplate, content, urlVariables).andExpect(status().isOk()), responseClass);
    }

    public <V> ResultActions performPost(String urlTemplate, V content, Object...urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
        setJwtToken(builder);
        String contentJsonString;
        if (content != null) {
            try {
                contentJsonString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            builder.content(contentJsonString).contentType(CONTENT_TYPE);
        }
        return mockMvc.perform(builder);
    }

    public <T> T performPostWithEmptyBody(String urlTemplate, Class<T> responseClass, Object... urlVariables) throws Exception {
        return readResponse(performPostWithEmptyBody(urlTemplate, urlVariables).andExpect(status().isOk()), responseClass);
    }

    public <V> ResultActions performPostWithEmptyBody(String urlTemplate, Object...urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
        setJwtToken(builder);
        return mockMvc.perform(builder);
    }

    public <T, V> T performPut(String urlTemplate, Class<T> responseClass, V content, Object... urlVariables) throws Exception {
        return readResponse(performPut(urlTemplate, content, urlVariables).andExpect(status().isOk()), responseClass);
    }

    public <V> ResultActions performPut(String urlTemplate, V content, Object...urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(urlTemplate, urlVariables);
        setJwtToken(builder);
        String contentJsonString;
        if (content != null) {
            try {
                contentJsonString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            builder.content(contentJsonString).contentType(CONTENT_TYPE);
        }
        return mockMvc.perform(builder);
    }

    public ResultActions performDelete(String urlTemplate, Object... urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate, urlVariables);
        setJwtToken(builder);
        return mockMvc.perform(builder);
    }

    <T> T readResponse(ResultActions result, Class<T> responseClass) throws IOException {
        MockHttpServletResponse response = result.andReturn().getResponse();
        byte[] content = result.andReturn().getResponse().getContentAsByteArray();
        return objectMapper.readValue(content, responseClass);
    }

    <T> T readResponse(ResultActions result, TypeReference<T> type) throws IOException {
        byte[] content = result.andReturn().getResponse().getContentAsByteArray();
        return objectMapper.readValue(content, type);
    }

    public void login (String username, String password) throws Exception {
        resetJwtTokenPair();
        ResultActions result = performPost("/auth/login", new LoginRequest(username, password)).andExpect(status().isOk());
        JwtTokenPair jwtTokenPair = readResponse(result, JwtTokenPair.class);
        this.accessToken = jwtTokenPair.getAccessToken();
        this.refreshToken = jwtTokenPair.getRefreshToken();
    }

    void logout() {
        resetJwtTokenPair();
    }

    public void resetJwtTokenPair() {
        this.accessToken = null;
        this.refreshToken = null;
    }

    public void setJwtToken(MockHttpServletRequestBuilder builder) {
        if (this.accessToken != null) {
            builder.header("Authorization", "Bearer " + this.accessToken);
        }
    }

    public ResultActions performLogin(String username, String password) throws Exception {
        return performPost("/auth/login", new LoginRequest(username, password));
    }

    protected User createUser(String name, String email, String password, String confirmPassword) throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return readResponse(performPost(USERS_REGISTER_USER_ROUTE + "?isMailRequired={isMailRequired}", request, false).andExpect(status().isOk()), User.class);
    }

    protected User createUser(RegisterUserRequest request) throws Exception {
        return performPost(USERS_REGISTER_USER_ROUTE + "?isMailRequired={isMailRequired}", User.class, request, false);
    }

    protected void activateUser(String userId) throws Exception {
        performPostWithEmptyBody(USERS_ACTIVATE_USER_CREDENTIALS_ROUTE, userId);
    }

    protected void deleteUser(String userId) throws Exception {
        performDelete(USERS_DELETE_USER_BY_ID_ROUTE, userId);
    }

    public String generateUsername() {
        return "user" + RandomStringUtils.randomAlphanumeric(5);
    }

    public String generateEmail() {
        return "user" + RandomStringUtils.randomAlphanumeric(5) + "@gmail.com";
    }

    public String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public String generatePassword() {
        UserPasswordPolicy passwordPolicy = securitySettings.getPasswordPolicy();
        return passwordGenerator.generatePassword(passwordPolicy.getMinimumLength(), passwordPolicy.getPasswordCharacterRules());
    }

    public Category getDefaultCategory() throws Exception {
        return performGet(CATEGORY_GET_DEFAULT_CATEGORY_ROUTE, Category.class);
    }

    protected ProductRequest createProductRequest() {
        ProductRequest product = ProductRequest
                .builder()
                .name(generateRandomString())
                .imageUrl(DEFAULT_IMAGE_URL)
                .variations(new ArrayList<>())
                .items(new ArrayList<>())
                .build();

        return product;
    }

    protected ProductRequest createProductRequestSample() {
        ProductRequest productRequest = createProductRequest();
        productRequest.setVariations(Collections.singletonList(createVariationRequest(1)));
        productRequest.setItems(Collections.singletonList(createItemRequest(0)));
        return productRequest;
    }

    protected ProductVariationRequest createVariationRequest() {
        return ProductVariationRequest
                .builder()
                .name(generateRandomString())
                .options(new ArrayList<>())
                .build();
    }

    protected ProductVariationRequest createVariationRequest(int totalOptions) {
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

    protected VariationOptionRequest createOptionRequest() {
        VariationOptionRequest option = VariationOptionRequest
                .builder()
                .name(generateRandomString())
                .build();
        return option;
    }

    protected ProductItemRequest createItemRequest(Integer requiredIndex, Integer...additionalIndexes) {
        List<Integer> indexes = new ArrayList<>();
        indexes.add(requiredIndex);
        indexes.addAll(List.of(additionalIndexes));
        return ProductItemRequest
                .builder()
                .quantity(100)
                .price(100f)
                .variationIndex(indexes)
                .build();
    }

    protected void deleteProduct(String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            Product product = performGet(PRODUCT_GET_PRODUCT_BY_ID_ROUTE, Product.class, id);
            if (product != null) {
                performDelete(PRODUCT_DELETE_PRODUCT_BY_ID_ROUTE, id);
            }
        }
    }

    public static class TimestampBasedComparator<T extends TimestampBased> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        }
    }
}
