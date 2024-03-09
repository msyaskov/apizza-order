package apizza.order.event.order;

import apizza.order.entity.Order;

public class NewOrderApplicationEvent extends OrderApplicationEvent {

    public NewOrderApplicationEvent(Object source, Order order) {
        super(source, order);
    }
}
