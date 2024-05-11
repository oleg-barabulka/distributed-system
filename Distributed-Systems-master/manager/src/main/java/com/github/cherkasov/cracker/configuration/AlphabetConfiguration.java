package com.github.cherkasov.cracker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AlphabetConfiguration {

    @Bean
    public List<String> alphabet() {
        return List.of("abcdefghijklmnopqrstuvwxyz0123456789".split(""));
    }
}
