package com.iagosaito.libraryapi.api.resources.service;

import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.repository.LoanRepository;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.model.Loan;
import com.iagosaito.libraryapi.domain.service.LoanService;
import com.iagosaito.libraryapi.domain.service.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    private LoanService loanService;

    @MockBean
    private LoanRepository loanRepository;

    @BeforeEach
    public void setup() {
        loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    public void mustSaveLoan() {
        Loan loan = createNewLoanWithId();

        when(loanRepository.existsByBookAndNotReturned(loan.getBook())).thenReturn(false);
        when(loanRepository.save(loan)).thenReturn(loan);

        Loan savedLoan = loanService.save(loan);

        assertThat(savedLoan.getLoanId()).isEqualTo(loan.getLoanId());
        assertThat(savedLoan.getLocalDate()).isEqualTo(loan.getLocalDate());
        assertThat(savedLoan.getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(savedLoan.getBook()).isEqualTo(loan.getBook());
    }

    @Test
    public void mustNotSaveLoanWithAlreadyLoaned() {

        Loan loan = createNewLoanWithId();

        when(loanRepository.existsByBookAndNotReturned(loan.getBook())).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(loan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void mustFindLoanById() {
        final long loanId = 1L;

        Loan loan = createNewLoanWithId();
        loan.setLoanId(loanId);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        Optional<Loan> optionalLoan = loanService.findById(loanId);

        assertThat(optionalLoan.isPresent()).isTrue();
        assertThat(optionalLoan.get().getLoanId()).isEqualTo(loan.getLoanId());
        assertThat(optionalLoan.get().getBook()).isEqualTo(loan.getBook());
        assertThat(optionalLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(optionalLoan.get().getReturned()).isEqualTo(loan.getReturned());
        assertThat(optionalLoan.get().getLocalDate()).isEqualTo(loan.getLocalDate());
    }

    @Test
    public void mustUpdateLoan() {
        Loan loan = createNewLoanWithId();
        loan.setReturned(true);

        when(loanRepository.save(loan)).thenReturn(loan);

        Loan savedLoan = loanService.update(loan);

        assertThat(savedLoan.getReturned()).isEqualTo(loan.getReturned());
    }

    private Loan createNewLoanWithId() {
        return Loan.builder()
                .loanId(1L)
                .book(Book.builder().id(1L).build())
                .customer("Iago Saito")
                .localDate(LocalDate.now())
                .build();
    }

}
