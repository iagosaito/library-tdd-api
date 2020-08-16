package com.iagosaito.libraryapi.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EntityInUseException extends BusinessException {

    public EntityInUseException(String e) {
        super(e);
    }
}
