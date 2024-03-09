package apizza.order.service.order;

import apizza.order.entity.Order;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public abstract class AbstractOrderServiceDecorator implements OrderService {

    private final OrderService delegate;

    protected AbstractOrderServiceDecorator(OrderService delegate) {
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public Order addOrder(@NonNull Order candidate) {
        return delegate.addOrder(candidate);
    }

    @NonNull
    @Override
    public List<Order> getOrders() {
        return delegate.getOrders();
    }

    @NonNull
    @Override
    public List<Order> getOrdersByUserId(@NonNull UUID userId) {
        return delegate.getOrdersByUserId(userId);
    }

    @NonNull
    @Override
    public Order getOrder(@NonNull UUID orderId) {
        return delegate.getOrder(orderId);
    }

    @Override
    public void removeOrder(@NonNull UUID orderId) {
        delegate.removeOrder(orderId);
    }

    @NonNull
    @Override
    public Order updateOrder(@NonNull UUID orderId, @NonNull Order candidate) {
        return delegate.updateOrder(orderId, candidate);
    }

    protected OrderService getDelegate() {
        return delegate;
    }
}
