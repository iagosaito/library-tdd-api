package com.iagosaito.libraryapi.api.resources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagosaito.libraryapi.api.dto.LoanModel;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.model.Loan;
import com.iagosaito.libraryapi.domain.service.LoanService;
import com.iagosaito.libraryapi.domain.model.LoanController;
import com.iagosaito.libraryapi.domain.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoanController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoanControllerTest {

    private static String LOAN_URI = "/api/loans";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    public void GivenLoan_WhenPost_CreateNewLoan() throws Exception {

        Book book = createNewBookWithId();
        LoanModel loanModel = createNewLoanModel();
        Loan loan = createNewLoanWithId();

        String json = new ObjectMapper().writeValueAsString(loanModel);

        given(bookService.getByIsbn(anyString())).willReturn(Optional.of(book));
        given(loanService.save(any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    private Loan createNewLoanWithId() {
        return Loan.builder()
                .loanId(1L)
                .book(createNewBookWithId())
                .localDate(LocalDate.now())
                .customer("Iago Saito")
                .returned(Boolean.TRUE)
            .build();
    }

    private Book createNewBookWithId() {
        return Book.builder()
                .id(1L)
                .author("Tim Maia")
                .title("Descobridor dos 7 Mares")
                .isbn("1234")
            .build();
    }

    private LoanModel createNewLoanModel() {
        return LoanModel.builder()
                .isbn("1234")
                .customer("Fuzz Person")
            .build();
    }
}
