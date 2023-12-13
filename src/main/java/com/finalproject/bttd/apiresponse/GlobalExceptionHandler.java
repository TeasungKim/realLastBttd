package com.finalproject.bttd.apiresponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(response.getStatus());
        response.setMessage("wrong information");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
