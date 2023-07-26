package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.mapper.OrderMapper;
import com.example.springbootmongodb.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.ORDER_CREATE_ORDER_ROUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.ORDER_GET_ORDER_BY_ID_ROUTE;

@RestController
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping(value = ORDER_CREATE_ORDER_ROUTE)
    @Operation(summary = "Tạo đơn hàng mới")
    Order create(@RequestBody OrderRequest request) {
       return orderMapper.fromEntity(orderService.create(request));
    }

    @GetMapping(value = ORDER_GET_ORDER_BY_ID_ROUTE)
    @Operation(summary = "Tìm đơn hàng theo id")
    Order getById(@PathVariable String orderId) {
        return orderMapper.fromEntity(orderService.findById(orderId));
    }

}
