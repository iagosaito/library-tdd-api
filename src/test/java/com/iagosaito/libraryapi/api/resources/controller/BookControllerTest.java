package com.iagosaito.libraryapi.api.resources.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagosaito.libraryapi.api.controller.BookController;
import com.iagosaito.libraryapi.api.dto.BookModel;
import com.iagosaito.libraryapi.config.LibraryTestConfig;
import com.iagosaito.libraryapi.domain.exception.BookNotFoundException;
import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.service.BookService;
import com.iagosaito.libraryapi.domain.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = BookController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(LibraryTestConfig.class)
public class BookControllerTest {

    private static String BOOK_URI = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    public void Given_Book_When_PostBook_Then_CreateNewBook() throws Exception {

        BookModel bookDTO = createNewBook();
        Book savedBook = Book.builder().id(1L).author("Iago").title("The Adventures of Iago").isbn("1").build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    public void Given_InvalidBook_When_PostBook_Then_ThrowException() throws Exception {

        String bookJson = new ObjectMapper().writeValueAsString(new BookModel());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    public void Given_DuplicatedISBNBook_When_PostBook_Then_ThrowException() throws Exception {

        Book savedBook = Book.builder().id(1L).author("Iago").title("The Adventures of Iago").isbn("1").build();
        String bookJson = new ObjectMapper().writeValueAsString(savedBook);

        String errorMessage = "ISBN já cadastrado";

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    public void When_GetBookById_Then_GetBookDetail() throws Exception {

        Long bookId = 1L;

        Book book = Book.builder()
                .id(bookId)
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .title(createNewBook().getTitle())
            .build();

        BDDMockito.given(bookService.findById(bookId))
                .willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_URI.concat("/" + bookId))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    public void When_GetBookByID_And_BookNotFound_Then_ThrowException() throws Exception {

        Long bookId = 1L;

        BDDMockito.given(bookService.findById(bookId))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_URI.concat("/" + bookId))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void When_DeleteBook_Then_ReturnNoContentStatus() throws Exception {

        Long bookId = 1L;

        BDDMockito.given(bookService.findById(Mockito.anyLong()))
                .willReturn(Optional.of(Book.builder().id(bookId).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_URI.concat("/" + bookId));

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    public void When_DeleteNonExistBook_Then_ReturnNotFoundStatus() throws Exception {

        Long bookId = 1L;

        BDDMockito.given(bookService.findById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_URI.concat("/" + bookId));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void Update_InexistentBook_Then_ReturnStatus404() throws Exception {

        long bookId = 1L;

        BookModel updatedBook = createNewBook();

        String bookModelJson = new ObjectMapper().writeValueAsString(updatedBook);

        BDDMockito.given(bookService.findById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_URI.concat("/" + bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookModelJson);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void Update_ValidBook_Then_ReturnBookAndStatus200() throws Exception {

        long bookId = 1L;

        BookModel bookModel = createNewBook();

        Book updatingBook = Book.builder()
                .id(1L)
                .author("Some Author")
                .title("Some Title")
                .isbn("1")
            .build();

        Book updatedBook = Book.builder()
                .id(1L)
                .author("Iago")
                .title("The Adventures of Iago")
                .isbn("1")
            .build();

        String bookModelJson = new ObjectMapper().writeValueAsString(bookModel);

        BDDMockito.given(bookService.findById(bookId))
                .willReturn(Optional.ofNullable(updatingBook));

        BDDMockito.given(bookService.save(Mockito.any()))
                .willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_URI.concat("/" + bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookModelJson);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    public void WhenFilterBook_Then_ReturnPagedBook() throws Exception {

        Book book = Book.builder()
                .id(1L)
                .author(createNewBook().getAuthor())
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(bookService.filter(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(book), PageRequest.of(0, 1), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_URI.concat(String.format("?title=%s&author=%s", book.getTitle(), book.getAuthor())))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(20))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookModel createNewBook() {
        return BookModel.builder().author("Iago").title("The Adventures of Iago").isbn("1").build();
    }

}
