package com.iagosaito.libraryapi.api.controller;

import com.iagosaito.libraryapi.api.dto.BookModel;
import com.iagosaito.libraryapi.api.exceptions.ApiErrors;
import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.exception.ObjectNotFoundException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookModel save(@RequestBody @Valid BookModel bookModel) {
        Book book = modelMapper.map(bookModel, Book.class);

        book = bookService.save(book);

        return modelMapper.map(book, BookModel.class);
    }

    @GetMapping("/{id}")
    public BookModel findById(@PathVariable Long id) {
        return bookService.findById(id)
                .map(book -> modelMapper.map(book, BookModel.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookModel> filter(BookModel bookModel,
                                  Pageable pageable) {
        Book book = modelMapper.map(bookModel, Book.class);

        Page<Book> bookPage = bookService.filter(book, pageable);

        List<BookModel> bookModelList = bookPage.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookModel.class))
                .collect(Collectors.toList());

        return new PageImpl<>(bookModelList, pageable, bookPage.getTotalElements());
    }

    @PutMapping("/{id}")
    public BookModel update(@PathVariable Long id, @RequestBody @Valid BookModel bookModel) {
        return bookService.findById(id).map(book -> {

            book.setTitle(bookModel.getTitle());
            book.setAuthor(bookModel.getAuthor());

            Book savedBook = bookService.save(book);

            return modelMapper.map(savedBook, BookModel.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = bookService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
 }

