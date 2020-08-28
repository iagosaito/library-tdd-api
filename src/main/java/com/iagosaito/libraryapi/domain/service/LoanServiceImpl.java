package com.iagosaito.libraryapi.domain.service;

import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Loan;
import com.iagosaito.libraryapi.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService{

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {

        if (loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned");
        }

        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> findById(Long idLoan) {
        return loanRepository.findById(idLoan);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }
}
