package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.model.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> findById(Long bookId);

    void delete(Book book);
}
