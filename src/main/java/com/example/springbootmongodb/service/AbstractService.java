package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.payment.PaymentStatus;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.OrderEntity;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.example.springbootmongodb.service.MomoPaymentServiceImpl.UNSUPPORTED_PAYMENT_METHOD_ERROR_MESSAGE;

public abstract class AbstractService {
    protected final int DEFAULT_TOKEN_LENGTH = 30;
    protected final String ACTIVATION_URL_PATTERN = "%s/auth/activate?activationToken=%s";
    protected final String PASSWORD_RESET_PATTERN = "%s/auth/resetPassword?passwordResetToken=%s";

    protected SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new AuthenticationServiceException("You aren't authorized to perform this operation!");
        }
        return (SecurityUser)authentication.getPrincipal();
    }

    public <T> boolean containsDuplicates(List<T> list, Function<T, String> getStringValue) {
        Set<String> checkSet = new HashSet<>();
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (T element : list) {
            if (!checkSet.add(getStringValue.apply(element))) {
                return true;
            }
        }
        return false;
    }

    protected void validateOrderState(OrderEntity order, OrderState... expectedStates) {
        OrderState actualState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        if (Arrays.stream(expectedStates).noneMatch(expectedState -> expectedState == actualState)) {
            throw new InvalidDataException(String.format("Order is %s",
                    actualState.getMessage().toLowerCase()));
        }
    }

    protected void validateUnexpectedOrderStates(OrderEntity order, OrderState... unexpectedStates) {
        OrderState actualState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        if (Arrays.stream(unexpectedStates).anyMatch(unexpectedState -> unexpectedState == actualState)) {
            throw new InvalidDataException(String.format("Order is %s",
                    actualState.getMessage().toLowerCase()));
        }
    }

    protected void validatePaymentMethod(PaymentMethod actualMethod, PaymentMethod expectedMethod) {
        if (actualMethod != expectedMethod) {
            throw new InvalidDataException(UNSUPPORTED_PAYMENT_METHOD_ERROR_MESSAGE);
        }
    }

    protected void validatePaymentStatus(PaymentStatus actualStatus, PaymentStatus expectedStatus) {
        if (actualStatus != expectedStatus) {
            throw new InvalidDataException(String.format("Payment is %s. It must be %s to perform this action",
                    actualStatus.name().toLowerCase(),
                    expectedStatus.name().toLowerCase()));
        }
    }
}
