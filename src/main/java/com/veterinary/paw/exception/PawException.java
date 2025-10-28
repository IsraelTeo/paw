package com.veterinary.paw.exception;

import com.veterinary.paw.enums.ApiErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PawException extends RuntimeException{
    private HttpStatus status;

    private String description;

    private List<String> reasons;

    public PawException (ApiErrorEnum error) {
        super(error.getMessage());
        this.status = error.getStatus();
        this.description = error.getMessage();
    }
}
