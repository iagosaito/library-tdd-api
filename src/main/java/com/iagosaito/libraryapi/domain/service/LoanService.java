package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.model.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> findById(Long idLoan);

    Loan update(Loan loan);
}

