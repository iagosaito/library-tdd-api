package com.iagosaito.libraryapi.api.resources.repository;

import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.model.Loan;
import com.iagosaito.libraryapi.domain.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static com.iagosaito.libraryapi.api.resources.repository.BookRepositoryTest.createNewBookWithoutId;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    public void existsByBookAlreadyLoanedTest() {
        Book book = createNewBookWithoutId("1234");
        book = entityManager.persist(book);

        Loan loan = createNewLoanWithoutId(book);
        entityManager.persist(loan);

        boolean returned = loanRepository.existsByBookAndNotReturned(book);

        assertThat(returned).isTrue();
    }

    public static Loan createNewLoanWithoutId(Book book) {
        return Loan.builder()
                .book(book)
                .customer("Iago Saito")
                .localDate(LocalDate.now())
                .build();
    }
}
