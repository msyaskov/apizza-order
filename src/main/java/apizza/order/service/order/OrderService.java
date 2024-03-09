package apizza.order.service.order;

import apizza.order.entity.Order;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    @NonNull
    Order addOrder(@NonNull Order candidate);

    @NonNull
    List<Order> getAllOrders();

    @NonNull
    List<Order> getAllOrdersByUserId(@NonNull UUID userId);

    @NonNull
    Order getOrder(@NonNull UUID orderId);

    void removeOrder(@NonNull UUID orderId);

    @NonNull
    Order updateOrder(@NonNull UUID orderId, @NonNull Order candidate);
}
