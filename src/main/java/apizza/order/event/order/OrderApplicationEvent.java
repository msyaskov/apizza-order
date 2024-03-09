package apizza.order.event.order;

import apizza.order.entity.Order;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class OrderApplicationEvent extends ApplicationEvent {

    @Getter
    private final Order order;

    public OrderApplicationEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}
