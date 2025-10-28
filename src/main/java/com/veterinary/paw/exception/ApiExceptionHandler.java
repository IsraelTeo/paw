package com.veterinary.paw.exception;

import com.veterinary.paw.dto.ErrorDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> reasons = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(ApiErrorEnum.VALIDATION_ERROR.getStatus())
                .body(new ErrorDTO(ApiErrorEnum.VALIDATION_ERROR.getMessage(), reasons)
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(ConstraintViolationException ex, WebRequest request) {
        List<String> reasons = ex.getConstraintViolations()
                .stream()
                .map(error -> String.format("%s - %s", error.getPropertyPath(), error.getMessage()))
                .toList();

        return ResponseEntity
                .status(ApiErrorEnum.VALIDATION_ERROR.getStatus())
                .body(new ErrorDTO(ApiErrorEnum.VALIDATION_ERROR.getMessage(), reasons));
    }

    @ExceptionHandler(PawException.class)
    public ResponseEntity<ErrorDTO> handlePawException(PawException ex) {
        return ResponseEntity
                .status(ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO(
                        ex.getDescription(),
                        ex.getReasons() != null ? ex.getReasons() : List.of(ex.getMessage())
                ));
    }
}
