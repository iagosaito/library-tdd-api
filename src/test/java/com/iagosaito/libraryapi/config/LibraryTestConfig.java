package com.iagosaito.libraryapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class LibraryTestConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
