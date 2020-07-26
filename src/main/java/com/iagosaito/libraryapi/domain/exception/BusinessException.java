package com.iagosaito.libraryapi.domain.exception;

import com.iagosaito.libraryapi.domain.model.Book;

public class BusinessException extends RuntimeException {

    public BusinessException(String e) {
        super(e);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
