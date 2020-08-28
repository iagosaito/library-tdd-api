package com.iagosaito.libraryapi.domain.repository;

import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT CASE WHEN (COUNT(l.loanId) >= 1) THEN true ELSE false END " +
            "FROM Loan l WHERE l.book = :book AND (l.returned IS NULL OR l.returned is false)")
    boolean existsByBookAndNotReturned(Book book);
}
