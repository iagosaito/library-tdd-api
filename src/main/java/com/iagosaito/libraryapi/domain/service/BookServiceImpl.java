package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.exception.BookNotFoundException;
import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.exception.EntityInUseException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.repository.BookRepository;
import com.iagosaito.libraryapi.domain.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(
                    String.format("Book with id %s cannot be deleted, because it's in use",
                            book.getId()));
        }
    }

    @Override
    public Page<Book> filter(Book book, Pageable pageable) {

        Example<Book> example = Example.of(book, ExampleMatcher.matchingAny()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );

        return bookRepository.findAll(example, pageable);
    }

    @Override
    public Optional<Book> getByIsbn(String isbn) {
        return Optional.empty();
    }
}
