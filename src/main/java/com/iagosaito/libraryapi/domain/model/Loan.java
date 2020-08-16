package com.iagosaito.libraryapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan")
public class Loan {

    private Long loanId;
    private String customer;
    private Book book;
    private LocalDate localDate;
    private Boolean returned;

    @PrePersist
    private void prePersist() {
        setLocalDate(LocalDate.now());
    }
}
