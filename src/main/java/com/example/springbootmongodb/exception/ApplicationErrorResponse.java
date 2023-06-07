package com.example.springbootmongodb.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@NoArgsConstructor
public class ApplicationErrorResponse {

    private int status;
    private String message;
    private Date timestamp;
    private String stackTrace;

    public ApplicationErrorResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus.value();
        this.message = message;
        this.timestamp = new Date();
        this.stackTrace = "";
    }

    public ApplicationErrorResponse(HttpStatus httpStatus, Exception exception) {

        this.status = httpStatus.value();
        this.message = exception.getMessage();
        this.timestamp = new Date();
        this.stackTrace = ExceptionUtils.getStackTrace(exception);
    }

}
