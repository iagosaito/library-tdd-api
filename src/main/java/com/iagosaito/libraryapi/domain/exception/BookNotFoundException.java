package com.iagosaito.libraryapi.domain.exception;

public class BookNotFoundException extends ObjectNotFoundException{

    public BookNotFoundException(String e) {
        super(e);
    }

    public BookNotFoundException(Long bookId) {
        this(String.format("The Book with id %d does not exists", bookId));
    }
}
