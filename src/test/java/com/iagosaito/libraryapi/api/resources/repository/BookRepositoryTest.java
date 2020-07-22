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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

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

        Book book = Book.builder()
                .isbn(isbn)
                .title("Teste")
                .author("Iago Saito")
                .build();


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

}
