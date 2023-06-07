package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.service.UserAddressService;
import com.example.springbootmongodb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserAddressService userAddressService;

    @Operation(tags = {"User"}, summary = "Returns a page of available users")
    @GetMapping(value = USERS_GET_USERS_ROUTE)
    PageData<User> getUsers(@Parameter(description = PAGE_NUMBER_DESCRIPTION)
                            @RequestParam(defaultValue = PAGE_NUMBER_DEFAULT_STRING_VALUE) int page,
                            @Parameter(description = PAGE_SIZE_DESCRIPTION)
                            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_STRING_VALUE) int pageSize,
                            @Parameter(description = SORT_ORDER_DESCRIPTION,
                                    examples = {@ExampleObject(name = "asc (Ascending)", value = "asc"),
                                            @ExampleObject(name = "desc (Descending)", value = "desc")})
                            @RequestParam(defaultValue = SORT_DIRECTION_DEFAULT_VALUE) String sortDirection,
                            @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                            @RequestParam(defaultValue = SORT_PROPERTY_DEFAULT_VALUE) String sortProperty) {
        //no validate sortOrder
        return userService.findUsers(new PageParameter(page, pageSize, sortDirection, sortProperty, ""));
    }

    @Operation(tags = {"User"}, summary = "Fetch the User object based on the provided userId")
    @GetMapping(value = USERS_GET_USER_BY_ID_ROUTE)
    User getUserById (@Parameter(description = "A string value representing the user id", required = true)
                      @PathVariable(name = "userId") String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException("User ID should be specified");
        }
        return userService.findById(userId);
    }

    @Operation(tags = {"User"}, summary = "Update user", description = "Update the User. " +
            "Specify existing User Id to update user. " +
            "Referencing non-existing User Id will cause 'Not Found' error.")
    @PostMapping(value = USERS_UPDATE_USER_ROUTE)
    User saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE), description = "User payload to update")
                  @RequestBody User user) {
        return userService.save(user);
    }

    @Operation(tags = {"User"}, summary = "Register new user", description = "Register new user. " +
            "When creating user, platform generates User Id as time-based UUID. " +
            "The newly created User Id will be present in the response. ")
    @PostMapping(value = USERS_REGISTER_USER_ROUTE)
    User regsiterUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration payload",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                      @RequestBody RegisterUserRequest registerUserRequest,
                      @Parameter(description = "A boolean indicates whether or not mail verification is required.")
                      @RequestParam(defaultValue = "false") boolean isMailRequired,
                      HttpServletRequest request) {
        return userService.register(registerUserRequest, request, isMailRequired);
    }

    @Operation(tags = {"User"}, summary = "Delete the User specified by userId and its credentials. A non-existent User Id will result in an error.")
    @DeleteMapping(value = USERS_DELETE_USER_BY_ID_ROUTE)
    void deleteUser(@Parameter(description = "A string value representing the user id", required = true)
                    @PathVariable(name = "userId") String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException("User ID should be specified");
        }
        userService.deleteById(userId);
    }

    @Operation(tags = {"User"}, summary = "Activate user by userId")
    @PostMapping(value = USERS_ACTIVATE_USER_CREDENTIALS_ROUTE)
    void activateUserByUserId(@Parameter(description = "A string value representing the user id", required = true)
                              @PathVariable(name = "userId") String userId) {
        userService.activateById(userId);
    }

}
