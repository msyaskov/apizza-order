package apizza.order.service.order;

import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import apizza.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order addOrder(Order candidate) {
        if (candidate.getUserId() == null) {
            throw new RuntimeException("invalid candidate");
        }

        if (candidate.getPizzas() == null || candidate.getPizzas().isEmpty()) {
            throw new RuntimeException("invalid candidate");
        }

        candidate.setDate(LocalDateTime.now());
        candidate.setPrice(candidate.getPizzas().stream().mapToDouble(Pizza::getPrice).sum());

        return orderRepository.save(candidate);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("order not found"));
    }

    @Override
    public void removeOrder(UUID orderId) {
        orderRepository.deleteById(orderId);
    }
}
