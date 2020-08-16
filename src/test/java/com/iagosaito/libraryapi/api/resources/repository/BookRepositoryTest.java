package com.iagosaito.libraryapi.api.resources.repository;

import com.iagosaito.libraryapi.domain.exception.BusinessException;
import com.iagosaito.libraryapi.domain.model.Book;
import com.iagosaito.libraryapi.domain.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void returnTrueWhenIsbnExists() {
        //given
        String isbn = "123";

        Book book = createNewBookWithoutId(isbn);

        entityManager.persist(book);

        //when
        boolean exists = bookRepository.existsByIsbn(isbn);

        //given
        Assertions.assertThat(exists).isTrue();
    }


    @Test
    public void returnFalseWhenIsbnDoesNotExists() {
        //given
        String isbn = "123";

        //when
        boolean exists = bookRepository.existsByIsbn(isbn);

        //given
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    public void returnBookWhenFindById() {
        Book book = createNewBookWithoutId("123");

        book = entityManager.persist(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        Assertions.assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    public void returnNonFoundBookWhenFindById() {
        Optional<Book> nonFoundBook = bookRepository.findById(999L);

        Assertions.assertThat(nonFoundBook.isPresent()).isFalse();
    }

    @Test
    public void returnBookWithIdWhenSaveBook() {
        Book book = createNewBookWithoutId("1234");

        book = bookRepository.save(book);

        Assertions.assertThat(book).isNotNull();
        Assertions.assertThat(book.getId()).isNotNull();
    }

    @Test
    public void deleteBookTest() {
        Book book = createNewBookWithoutId("123");
        book = bookRepository.save(book);

        bookRepository.delete(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }

    private Book createNewBookWithoutId(String isbn) {
        return Book.builder()
                .isbn(isbn)
                .title("Teste")
                .author("Iago Saito")
                .build();
    }

}
