package apizza.order.event.pizza;

import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class PizzaApplicationEvent extends ApplicationEvent {

    @Getter
    private final Pizza pizza;

    public PizzaApplicationEvent(Object source, Pizza pizza) {
        super(source);
        this.pizza = pizza;
    }

}
