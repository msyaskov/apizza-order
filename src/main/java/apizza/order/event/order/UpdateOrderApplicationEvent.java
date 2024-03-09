package apizza.order.event.order;

import apizza.order.entity.Order;

public class UpdateOrderApplicationEvent extends OrderApplicationEvent {

    public UpdateOrderApplicationEvent(Object source, Order order) {
        super(source, order);
    }
}
