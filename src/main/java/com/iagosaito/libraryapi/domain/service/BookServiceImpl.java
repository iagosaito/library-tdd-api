package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.exception.BookNotFoundException;
import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.repository.BookRepository;
import com.iagosaito.libraryapi.domain.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return bookRepository.findById(bookId);
    }

    @Override
    @Transactional
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null!!");
        }

        try {
            bookRepository.delete(book);
            bookRepository.flush();
        } catch (EmptyResultDataAccessException e) {
            throw new BookNotFoundException(book.getId());
        }
    }
}
