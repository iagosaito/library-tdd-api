package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> findById(Long bookId);

    void delete(Book book);

    Page<Book> filter(Book book, Pageable pageable);

    Optional<Book> getByIsbn(String isbn);
}
