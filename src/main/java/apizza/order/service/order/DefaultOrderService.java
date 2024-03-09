package apizza.order.service.order;

import apizza.order.entity.Order;
import apizza.order.entity.OrderStatus;
import apizza.order.entity.Pizza;
import apizza.order.repository.OrderRepository;
import apizza.order.service.InvalidCandidateException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;

    @NonNull
    @Override
    public Order addOrder(@NonNull Order candidate) {
        if (candidate.getUserId() == null) {
            throw new InvalidCandidateException("Candidate's userId must not be null");
        }

        if (candidate.getPizzas() == null || candidate.getPizzas().isEmpty()) {
            throw new InvalidCandidateException("Candidate's pizzas must not be null or empty");
        }

        candidate.setId(null);
        candidate.setDate(LocalDateTime.now().withNano(0));
        candidate.setPrice(candidate.getPizzas().stream().mapToDouble(Pizza::getPrice).sum());
        candidate.setStatus(OrderStatus.PENDING);

        return orderRepository.save(candidate);
    }

    @NonNull
    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @NonNull
    @Override
    public List<Order> getOrdersByUserId(@NonNull UUID userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @NonNull
    @Override
    public Order getOrder(@NonNull UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(msgOrderNotFoundById(orderId)));
    }

    @Override
    public void removeOrder(@NonNull UUID orderId) {
        orderRepository.deleteById(orderId);
    }

    @NonNull
    @Override
    @Transactional
    public Order updateOrder(@NonNull UUID orderId, @NonNull Order candidate) {
        Order order = getOrder(orderId);
        if (candidate.getStatus() != null) {
            order.setStatus(candidate.getStatus());
        }

        return order;
    }

    private String msgOrderNotFoundById(UUID orderId) {
        return "Order[id=%s] not found".formatted(orderId);
    }
}
