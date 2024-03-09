package apizza.order.service.order;

import apizza.order.entity.Order;
import apizza.order.event.order.NewOrderApplicationEvent;
import apizza.order.event.order.UpdateOrderApplicationEvent;
import apizza.order.service.order.AbstractOrderServiceDecorator;
import apizza.order.service.order.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class OrderApplicationEventPublisherOrderServiceDecorator extends AbstractOrderServiceDecorator {

    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderApplicationEventPublisherOrderServiceDecorator(OrderService delegate,
                                                                  ApplicationEventPublisher applicationEventPublisher) {
        super(delegate);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @NonNull
    @Override
    public Order addOrder(@NonNull Order candidate) {
        Order added = super.addOrder(candidate);
        applicationEventPublisher.publishEvent(new NewOrderApplicationEvent(this, added));

        return added;
    }

    @NonNull
    @Override
    public Order updateOrder(@NonNull UUID orderId, @NonNull Order candidate) {
        Order updated = super.updateOrder(orderId, candidate);
        applicationEventPublisher.publishEvent(new UpdateOrderApplicationEvent(this, updated));

        return updated;
    }
}
