package com.iagosaito.libraryapi.domain.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Loan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long loanId;

    @Column
    private String customer;

    @ManyToOne
    @JoinColumn
    private Book book;

    @Column
    private LocalDate localDate;

    @Column
    private Boolean returned;

    @PrePersist
    private void prePersist() {
        setLocalDate(LocalDate.now());
    }
}
