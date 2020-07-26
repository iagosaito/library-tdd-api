package com.iagosaito.libraryapi.domain.exception;

public abstract class ObjectNotFoundException extends BusinessException {

    public ObjectNotFoundException(String e) {
        super(e);
    }
}
