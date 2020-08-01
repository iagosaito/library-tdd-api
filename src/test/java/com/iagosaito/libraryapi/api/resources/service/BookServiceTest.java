package com.iagosaito.libraryapi.api.resources.service;

import com.iagosaito.libraryapi.domain.exception.BookNotFoundException;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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
        Book book = createValidBookWithoutId();

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
    public void mustNotSaveBookWithDuplicateISBN() {
        Book book = createValidBookWithoutId();

        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString()))
                .thenReturn(true);

        Throwable businessException = Assertions.catchThrowable(() -> bookService.save(book));

        Assertions.assertThat(businessException)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já cadastrado");
    }

    @Test
    public void mustFindBookById() {
        Long idBook = 1L;

        Book book = createValidBookWithoutId();
        book.setId(idBook);

        Mockito.when(bookRepository.findById(idBook)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.findById(idBook);

        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    public void mustNotFoundBookByIdThatDoesnExist() {
        Long idBook = 1L;

        Mockito.when(bookRepository.findById(idBook)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.findById(idBook);

        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    public void mustDeleteBookById() {
        Long idBook = 1L;

        Book book = createValidBookWithoutId();
        book.setId(idBook);

        Mockito.doNothing().when(bookRepository).delete(book);

        bookService.delete(book);

        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    public void mustThrowExceptionWhenDeleteBookWithNullId() {
        Book book = createValidBookWithoutId();

        Throwable illegalArgumentException =
                Assertions.catchThrowable(() -> bookService.delete(book));

        Assertions.assertThat(illegalArgumentException)
                .hasMessage("ID cannot be null!!");

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    public void mustThrowExceptionWhenDeleteBookWithNonExistentId() {
        final long idBook = 999L;

        Book book = createValidBookWithoutId();
        book.setId(idBook);

        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(bookRepository).delete(book);

        Throwable bookNotFoundException =
                Assertions.catchThrowable(() -> bookService.delete(book));

        Assertions.assertThat(bookNotFoundException)
                .hasMessage(String.format("The Book with id %d does not exists", idBook));
    }

    private Book createValidBookWithoutId() {
        return Book.builder()
                .author("Iago Saito")
                .title("The Adventures of Iago Saito")
                .isbn("1234").build();
    }
}
