package apizza.order.controller;

import apizza.order.dto.OrderDto;
import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import apizza.order.mapper.OrderMapper;
import apizza.order.service.order.OrderService;
import apizza.order.service.pizza.PizzaService;
import apizza.order.validation.group.PatchCandidateGroup;
import apizza.order.validation.group.PostCandidateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/orders/{orderId}") // TODO for admin если заказ не пользователя
    public OrderDto getOrder(@PathVariable UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return mapOrderToOrderDto(order);
    }

    @GetMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE) // TODO for admin
    public Collection<OrderDto> getOrders() {
        return orderService.getAllOrders().stream()
                .map(this::mapOrderToOrderDto)
                .toList();
    }

    @GetMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE,
            params = "userId") // TODO for admin
    public Collection<OrderDto> getOrdersByUserId(@RequestParam UUID userId) {
        return orderService.getAllOrdersByUserId(userId).stream()
                .map(this::mapOrderToOrderDto)
                .toList();
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE) // TODO for anybody
    public OrderDto postOrder(@RequestBody @Validated(PostCandidateGroup.class) OrderDto candidateDto) {
        List<Pizza> orderedPizzas = pizzaService.getPizzas(candidateDto.getPizzas());

        Order candidate = Order.builder()
                .userId(UUID.randomUUID()) // TODO User authentication
                .pizzas(orderedPizzas)
                .build();

        Order order = orderService.addOrder(candidate);
        return mapOrderToOrderDto(order);
    }

    // TODO for admin
    @PatchMapping(path = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDto patchOrder(@PathVariable UUID orderId,
                               @RequestBody @Validated(PatchCandidateGroup.class) OrderDto candidateDto) {
        Order candidate = Order.builder()
                .status(candidateDto.getStatus())
                .build();

        Order order = orderService.updateOrder(orderId, candidate);
        return mapOrderToOrderDto(order);
    }

    private OrderDto mapOrderToOrderDto(Order order) {
        return orderMapper.toDto(order);
    }

}
