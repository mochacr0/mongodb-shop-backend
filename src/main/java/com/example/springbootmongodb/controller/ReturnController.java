package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.OrderReturn;
import com.example.springbootmongodb.common.data.ReturnRequest;
import com.example.springbootmongodb.common.data.mapper.ReturnMapper;
import com.example.springbootmongodb.common.validator.Required;
import com.example.springbootmongodb.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;


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

    @GetMapping(value = ORDER_RETURN_GET_RETURN_BY_ID_ROUTE)
    @Operation(summary = "Truy xuất đơn trả hàng/hoàn tiền theo id")
    OrderReturn getReturnById(@PathVariable String returnId) {
        return returnMapper.fromEntity(returnService.findById(returnId));
    }

    @PostMapping(value = ORDER_RETURN_CONFIRM_RETURN_PROCESSING_ROUTE)
    @Operation(summary = "Xác nhận đơn trả hàng/hoàn tiền đang được xử lý")
    OrderReturn confirmProcessing(@PathVariable String returnId) {
        return returnMapper.fromEntity(returnService.confirmProcessing(returnId));
    }
}
