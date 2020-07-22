package com.iagosaito.libraryapi.api.exceptions;

import com.iagosaito.libraryapi.domain.exception.BusinessException;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class ApiErrors {

    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });
    }

    public ApiErrors(BusinessException businessException) {
        this.errors = Collections.singletonList(businessException.getMessage());
    }
}
