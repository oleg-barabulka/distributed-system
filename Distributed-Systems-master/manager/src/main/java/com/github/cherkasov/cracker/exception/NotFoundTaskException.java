package com.github.cherkasov.cracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundTaskException extends Exception {

    public NotFoundTaskException(String message) {
        super(message);
    }
}
