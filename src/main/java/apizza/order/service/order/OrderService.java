package apizza.order.service.order;

import apizza.order.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order addOrder(Order candidate);

    List<Order> getAllOrders();

    Order getOrder(UUID orderId);

    void removeOrder(UUID orderId);

}
