package com.iagosaito.libraryapi.api.controller;

import com.iagosaito.libraryapi.api.dto.LoanModel;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.model.Loan;
import com.iagosaito.libraryapi.domain.service.BookService;
import com.iagosaito.libraryapi.domain.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/loans")
public class LoanController {

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanModel loanModel) {

        Book book = bookService.getByIsbn(loanModel.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN not found!!"));

        Loan loan = new ModelMapper().map(loanModel, Loan.class);

        loan = loanService.save(loan);

        return loan.getLoanId();
    }
}
