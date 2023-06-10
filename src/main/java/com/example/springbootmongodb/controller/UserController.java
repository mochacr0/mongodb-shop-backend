package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@Tag(name = "User")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Returns a page of available users")
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

    @Operation(summary = "Fetch the User object based on the provided userId")
    @GetMapping(value = USERS_GET_USER_BY_ID_ROUTE)
    User getUserById (@Parameter(description = "A string value representing the user id", required = true)
                      @PathVariable(name = "userId") String userId) {
        return userService.findById(userId);
    }

    @Operation(summary = "Fetch the Current User object")
    @GetMapping(value = USERS_GET_CURRENT_USER_ROUTE)
    User getCurrentUser () {
        return userService.findCurrentUser();
    }

    @Operation(summary = "Update current user", description = "Update the Current User. " +
            "Referencing non-existing User will cause 'Not Found' error.")
    @PutMapping(value = USERS_UPDATE_USER_ROUTE)
    User saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                        description = "User payload to update")
                  @RequestBody User user) {
        return userService.saveCurrentUser(user);
    }

    @Operation(summary = "Register new user", description = "Register new user. " +
            "When creating user, platform generates User Id. " +
            "The newly created User Id will be present in the response. ")
    @PostMapping(value = USERS_REGISTER_USER_ROUTE)
    User regsiterUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
                                                                            description = "User registration payload")
                      @RequestBody RegisterUserRequest registerUserRequest,
                      @Parameter(description = "A boolean indicates whether or not mail verification is required.")
                      @RequestParam(defaultValue = "false") boolean isMailRequired,
                      HttpServletRequest request) {
        return userService.register(registerUserRequest, request, isMailRequired);
    }

    @Operation(summary = "Delete the User specified by userId and its credentials. A non-existent User Id will result in an error.")
    @DeleteMapping(value = USERS_DELETE_USER_BY_ID_ROUTE)
    void deleteUser(@Parameter(description = "A string value representing the user id", required = true)
                    @PathVariable(name = "userId") String userId) {
        userService.deleteById(userId);
    }

    @Operation(summary = "Activate user by userId")
    @PostMapping(value = USERS_ACTIVATE_USER_CREDENTIALS_ROUTE)
    void activateUserByUserId(@Parameter(description = "A string value representing the user id", required = true)
                              @PathVariable(name = "userId") String userId) {
        userService.activateById(userId);
    }

}
