package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.repository.BookRepository;
import com.iagosaito.libraryapi.domain.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("ISBN já cadastrado");
        }

        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        return null;
    }

    @Override
    public void delete(Book book) {

    }
}
