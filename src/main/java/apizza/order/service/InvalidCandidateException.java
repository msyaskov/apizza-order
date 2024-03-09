package apizza.order.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCandidateException extends RuntimeException{

    public InvalidCandidateException(String message) {
        super(message);
    }

    public InvalidCandidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
