package apizza.order.controller;

import apizza.order.dto.OrderDto;
import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import apizza.order.mapper.OrderMapper;
import apizza.order.service.order.OrderService;
import apizza.order.service.pizza.PizzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PizzaService pizzaService;
    private final OrderMapper orderMapper;

    @GetMapping("/orders") // TODO for admin
    public Collection<OrderDto> getOrders() {
        return orderService.getAllOrders().stream()
                .map(this::mapOrderToOrderDto)
                .toList();
    }

    @PostMapping("/orders") // TODO for anybody
    @ResponseStatus(HttpStatus.OK)
    public OrderDto postOrder(OrderDto candidateDto) {
        if (candidateDto.getPizzas().isEmpty()) {
            throw new RuntimeException("Empty order");
        }

        List<Pizza> orderedPizzas = pizzaService.getPizzas(candidateDto.getPizzas());

        Order candidate = Order.builder()
                .userId(UUID.randomUUID()) // TODO User authentication
                .pizzas(orderedPizzas)
                .build();

        Order order = orderService.addOrder(candidate);
        return mapOrderToOrderDto(order);
    }

    @GetMapping("/orders/{orderId}") // TODO for admin если заказ не пользователя
    public OrderDto getOrder(@PathVariable UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return mapOrderToOrderDto(order);
    }

    @DeleteMapping("/orders/{orderId}") // TODO for admin если заказ не пользователя
    public void deleteOrder(@PathVariable UUID orderId) {
        orderService.removeOrder(orderId);
    }

    private OrderDto mapOrderToOrderDto(Order order) {
        return orderMapper.toDto(order);
    }

}
