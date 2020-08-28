package com.iagosaito.libraryapi.api.resources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagosaito.libraryapi.api.controller.LoanController;
import com.iagosaito.libraryapi.api.dto.LoanModel;
import com.iagosaito.libraryapi.api.dto.ReturnedLoanInput;
import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.model.Loan;
import com.iagosaito.libraryapi.domain.service.LoanService;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoanController.class)
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

    @Test
    public void GivenLoanWithNonExistentISBN_WhenPost_ReturnStatus400() throws Exception {

        final String errorMessage = "ISBN not found!!";

        LoanModel loanModel = createNewLoanModel();
        String json = new ObjectMapper().writeValueAsString(loanModel);

        given(bookService.getByIsbn(anyString())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    public void GivenBookAlreadyLoaned_WhenPost_ReturnStatus400() throws Exception {

        final String errorMessage = "Book already loaned";

        LoanModel loanModel = createNewLoanModel();
        String json = new ObjectMapper().writeValueAsString(loanModel);

        Book book = createNewBookWithId();

        given(bookService.getByIsbn(anyString())).willReturn(Optional.of(book));
        given(loanService.save(any(Loan.class))).willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    public void GivenLoan_WhenReturnBook_UpdateLoan() throws Exception {
        ReturnedLoanInput returnedLoanInput = new ReturnedLoanInput(true);

        Loan loan = createNewLoanWithId();

        given(loanService.findById(anyLong())).willReturn(Optional.of(loan));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(LOAN_URI.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(returnedLoanInput));

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).update(loan);
        verify(loanService, times(1)).findById(anyLong());
    }

    @Test
    public void GivenNonExistLoan_WhenUpdate_ThenReturnStatus404() throws Exception {

        ReturnedLoanInput returnedLoanInput = new ReturnedLoanInput(true);

        given(loanService.findById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(LOAN_URI.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(returnedLoanInput));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        verify(loanService, never()).update(any(Loan.class));
        verify(loanService, times(1)).findById(anyLong());
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
