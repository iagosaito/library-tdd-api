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

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanModel loanModel) {

        Book book = bookService.getByIsbn(loanModel.getIsbn()).get();
        Loan loan = new ModelMapper().map(loanModel, Loan.class);

        loan = loanService.save(loan);

        return loan.getLoanId();
    }
}
