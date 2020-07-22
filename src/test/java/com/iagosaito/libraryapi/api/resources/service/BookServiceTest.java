package com.iagosaito.libraryapi.api.resources.service;

import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.repository.BookRepository;
import com.iagosaito.libraryapi.domain.service.BookService;
import com.iagosaito.libraryapi.domain.service.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    public void mustSaveBookTest() {
        //given
        Book book = createValidBook();

        Mockito.when(bookRepository.save(book))
                .thenReturn(
                        Book.builder()
                            .id(1L)
                            .author("Iago Saito")
                            .title("The Adventures of Iago Saito")
                            .isbn("1234").build()
                );

        //when
        Book savedBook = bookService.save(book);

        //then
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("1234");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("The Adventures of Iago Saito");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Iago Saito");
    }

    @Test
    public void shouldNotSaveABookWithDuplicateISBN() {
        Book book = createValidBook();

        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString()))
                .thenReturn(true);

        Throwable businessException = Assertions.catchThrowable(() -> bookService.save(book));

        Assertions.assertThat(businessException)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já cadastrado");
    }

    private Book createValidBook() {
        return Book.builder()
                .author("Iago Saito")
                .title("The Adventures of Iago Saito")
                .isbn("1234").build();
    }
}
