package com.iagosaito.libraryapi.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanModel {
    private String isbn;
    private String customer;
}
