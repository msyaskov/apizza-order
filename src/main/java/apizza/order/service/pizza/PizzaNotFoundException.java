package apizza.order.service.pizza;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PizzaNotFoundException extends RuntimeException {

    public PizzaNotFoundException(String message) {
        super(message);
    }

    public PizzaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
