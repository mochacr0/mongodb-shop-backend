package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.OrderReturn;
import com.example.springbootmongodb.common.data.ReturnRequest;
import com.example.springbootmongodb.common.data.mapper.ReturnMapper;
import com.example.springbootmongodb.common.validator.Required;
import com.example.springbootmongodb.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.springbootmongodb.controller.ControllerConstants.ORDER_RETURN_REQUEST_RETURN_ROUTE;


@RestController
@RequiredArgsConstructor
@Tag(name = "Order Return")
public class ReturnController {
    private final ReturnService returnService;
    private final ReturnMapper returnMapper;

    @PostMapping(value = ORDER_RETURN_REQUEST_RETURN_ROUTE)
    @Operation(summary = "Gửi yêu cầu trả hàng/hoàn tiền")
    OrderReturn request(@RequestBody ReturnRequest request) {
        return returnMapper.fromEntity(returnService.create(request));
    }
}
