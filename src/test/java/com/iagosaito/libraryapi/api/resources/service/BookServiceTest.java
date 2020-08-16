package com.iagosaito.libraryapi.api.resources.service;

import com.iagosaito.libraryapi.domain.exception.BookNotFoundException;
import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.exception.EntityInUseException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.repository.BookRepository;
import com.iagosaito.libraryapi.domain.service.BookService;
import com.iagosaito.libraryapi.domain.service.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

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
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("1234");
        assertThat(savedBook.getTitle()).isEqualTo("The Adventures of Iago Saito");
        assertThat(savedBook.getAuthor()).isEqualTo("Iago Saito");
    }

    @Test
    public void mustNotSaveBookWithDuplicateISBN() {
        Book book = createValidBookWithoutId();

        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString()))
                .thenReturn(true);

        Throwable businessException = Assertions.catchThrowable(() -> bookService.save(book));

        assertThat(businessException)
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

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    public void mustNotFoundBookByIdThatDoesnExist() {
        Long idBook = 1L;

        Mockito.when(bookRepository.findById(idBook)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.findById(idBook);

        assertThat(foundBook.isPresent()).isFalse();
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

        assertThat(illegalArgumentException)
                .hasMessage("ID cannot be null!!");

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    public void mustThrowNotFoundExceptionWhenDeleteBookWithNonExistentId() {
        final long idBook = 999L;

        Book book = createValidBookWithoutId();
        book.setId(idBook);

        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(bookRepository).delete(book);

        Throwable bookNotFoundException =
                Assertions.catchThrowable(() -> bookService.delete(book));

        assertThat(bookNotFoundException)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage(String.format("The Book with id %d does not exists", idBook));
    }

    @Test
    public void mustThrowInUseExceptionWhenDeleteBookThatsInUse() {
        final long idBook = 999L;

        Book book = createValidBookWithoutId();
        book.setId(idBook);

        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(bookRepository).delete(book);

        Throwable bookInUseException =
                Assertions.catchThrowable(() -> bookService.delete(book));

        assertThat(bookInUseException)
                .isInstanceOf(EntityInUseException.class)
                .hasMessage(String.format("Book with id %s cannot be deleted, because it's in use",
                        book.getId()));
    }

    @Test
    public void mustFilterBookByTitleAndAuthor() {

        Book book = createValidBookWithoutId();
        book.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> bookList = Collections.singletonList(book);

        Page<Book> bookPage = new PageImpl<>(bookList, pageRequest, 1);

        Mockito.when( bookRepository.findAll(any(Example.class), any(PageRequest.class)) )
                .thenReturn(bookPage);

        Page<Book> result = bookService.filter(book, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).isEqualTo(bookList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    private Book createValidBookWithoutId() {
        return Book.builder()
                .author("Iago Saito")
                .title("The Adventures of Iago Saito")
                .isbn("1234").build();
    }
}
