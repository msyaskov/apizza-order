package apizza.order.controller;

import apizza.order.dto.OrderDto;
import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import apizza.order.mapper.OrderMapper;
import apizza.order.service.order.OrderService;
import apizza.order.service.pizza.PizzaService;
import apizza.order.util.logging.Logging;
import apizza.order.validation.group.PatchCandidateGroup;
import apizza.order.validation.group.PostCandidateGroup;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Logging
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PizzaService pizzaService;
    private final OrderMapper orderMapper;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a order by orderId")
    @PostAuthorize("(hasAuthority('USER') and returnObject.userId.equals(principal)) or hasAuthority('ADMIN')")
    @GetMapping(path = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDto getOrder(@PathVariable UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return mapOrderToOrderDto(order);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all orders")
    @GetMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<OrderDto> getOrders() {
        return orderService.getOrders().stream()
                .map(this::mapOrderToOrderDto)
                .toList();
    }

    @Operation(summary = "Get all orders by ids")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and #userId.equals(principal))")
    @GetMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE,
            params = "userId")
    public Collection<OrderDto> getOrdersByUserId(@RequestParam UUID userId) {
        return orderService.getOrdersByUserId(userId).stream()
                .map(this::mapOrderToOrderDto)
                .toList();
    }

    @Transactional
    @PreAuthorize("authenticated")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new order by candidate")
    @PostMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDto postOrder(@RequestBody @Validated(PostCandidateGroup.class) OrderDto candidateDto,
                              Authentication authentication) {
        List<Pizza> orderedPizzas = pizzaService.getPizzas(candidateDto.getPizzas());

        Order candidate = Order.builder()
                .userId((UUID) authentication.getPrincipal())
                .pizzas(orderedPizzas)
                .build();

        Order order = orderService.addOrder(candidate);
        return mapOrderToOrderDto(order);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Change order by candidate")
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
