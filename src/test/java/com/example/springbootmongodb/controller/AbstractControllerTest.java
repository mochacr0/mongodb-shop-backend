package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.security.JwtTokenPair;
import com.example.springbootmongodb.security.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static com.example.springbootmongodb.controller.ControllerConstants.USERS_DELETE_USER_BY_ID_ROUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.USERS_REGISTER_USER_ROUTE;
import static com.example.springbootmongodb.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${security.maxFailedLoginAttempts}")
    protected int maxFailedLoginAttempts;
    @Value("${security.failedLoginLockExpirationMillis}")
    protected long failedLoginLockExpirationMillis;
    @Value("${security.failedLoginIntervalMillis}")
    protected long failedLoginIntervalMillis;
    private String accessToken;
    private String refreshToken;

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
        return readResponse(performPost(urlTemplate, content, urlVariables), responseClass);
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
        return readResponse(performPostWithEmptyBody(urlTemplate, urlVariables), responseClass);
    }

    public <V> ResultActions performPostWithEmptyBody(String urlTemplate, Object...urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
        setJwtToken(builder);
        return mockMvc.perform(builder);
    }

//    public ResultActions performPost(String urlTemplate, Object...urlVariables) throws Exception {
//        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
//        String contentJsonString;
//        return mockMvc.perform(builder);
//    }

    public ResultActions performDelete(String urlTemplate, Object... urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate, urlVariables);
        setJwtToken(builder);
        return mockMvc.perform(builder);
    }

    <T> T readResponse(ResultActions result, Class<T> responseClass) throws IOException {
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
        return readResponse(performPost(USERS_REGISTER_USER_ROUTE, request).andExpect(status().isOk()), User.class);
    }

    protected User createUser(RegisterUserRequest request) throws Exception {
        return performPost(USERS_REGISTER_USER_ROUTE, User.class, request);
    }

    protected void deleteUser(String userId) throws Exception {
        performDelete(USERS_DELETE_USER_BY_ID_ROUTE, userId.toString()).andExpect(status().isOk());
    }

    public String getRandomUsername() {
        return "user" + RandomStringUtils.randomAlphanumeric(5);
    }

    public String getRandomEmail() {
        return "user" + RandomStringUtils.randomAlphanumeric(5) + "@gmail.com";
    }

    public String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
