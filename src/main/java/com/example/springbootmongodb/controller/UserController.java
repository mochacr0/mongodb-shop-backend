package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.mapper.UserMapper;
import com.example.springbootmongodb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@Tag(name = "User")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @Operation(summary = "Phân trang danh sách user")
    @GetMapping(value = USERS_GET_USERS_ROUTE)
    PageData<User> getUsers(@Parameter(description = PAGE_NUMBER_DESCRIPTION)
                            @RequestParam(defaultValue = PAGE_NUMBER_DEFAULT_STRING_VALUE) int page,
                            @Parameter(description = PAGE_SIZE_DESCRIPTION)
                            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_STRING_VALUE) int pageSize,
                            @Parameter(description = SORT_ORDER_DESCRIPTION,
                                    examples = {@ExampleObject(name = "asc (Tăng dần)", value = "asc"),
                                            @ExampleObject(name = "desc (Giảm dần)", value = "desc")})
                            @RequestParam(defaultValue = SORT_DIRECTION_DEFAULT_VALUE) String sortDirection,
                            @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                            @RequestParam(defaultValue = SORT_PROPERTY_DEFAULT_VALUE) String sortProperty) {
        //no validate sortOrder
        return userService.findUsers(PageParameter
                .builder()
                .page(page)
                .pageSize(pageSize)
                .sortDirection(sortDirection)
                .sortProperty(sortProperty)
                .textSearch("")
                .build());
    }

    @Operation(summary = "Tìm user theo Id")
    @GetMapping(value = USERS_GET_USER_BY_ID_ROUTE)
    User getUserById (@Parameter(description = "A string value representing the user id", required = true)
                      @PathVariable(name = "userId") String userId) {
        return mapper.toUser(userService.findById(userId));
    }

    @Operation(summary = "Tìm user hiện đang đăng nhập")
    @GetMapping(value = USERS_GET_CURRENT_USER_ROUTE)
    User getCurrentUser () {
        return mapper.toUser(userService.findCurrentUser());
    }

    @Operation(summary = "Update user hiện đăng đăng nhâp")
    @PutMapping(value = USERS_UPDATE_USER_ROUTE)
    User saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                  @RequestBody User user) {
        return mapper.toUser(userService.saveCurrentUser(user));
    }

    @Operation(summary = "Đăng ký user")
    @PostMapping(value = USERS_REGISTER_USER_ROUTE)
    User regsiterUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                      @RequestBody RegisterUserRequest registerUserRequest,
                      @Parameter(description = "Có cần gửi email xác nhận hay không. (Mặc định là true. Chỉ set false khi test)")
                      @RequestParam(defaultValue = "true") boolean isMailRequired,
                      HttpServletRequest request) {
        return mapper.toUser(userService.register(registerUserRequest, request, isMailRequired));
    }

    @Operation(summary = "Xóa user theo Id")
    @DeleteMapping(value = USERS_DELETE_USER_BY_ID_ROUTE)
    void deleteUser(@Parameter(description = "A string value representing the user id", required = true)
                    @PathVariable(name = "userId") String userId) {
        userService.deleteById(userId);
    }

    @Operation(summary = "Kích hoạt tài khoản. (API này chỉ dùng để test)")
    @PostMapping(value = USERS_ACTIVATE_USER_CREDENTIALS_ROUTE)
    void activateUserByUserId(@Parameter(description = "Id của tài khoản muốn kích hoạt", required = true)
                              @PathVariable(name = "userId") String userId) {
        userService.activateById(userId);
    }

}
