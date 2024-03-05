package apizza.order.mapper;

import apizza.order.dto.OrderDto;
import apizza.order.dto.PizzaDto;
import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMapper implements Mapper<Order, OrderDto> {

    @Override
    public OrderDto toDto(Order entity) {
        if (entity == null) {
            return null;
        }

        final List<UUID> pizzas = new LinkedList<>();
        if (entity.getPizzas() != null) {
            entity.getPizzas().stream()
                    .map(Pizza::getId)
                    .collect(Collectors.toCollection(() -> pizzas));
        }

        return OrderDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .date(entity.getDate())
                .price(entity.getPrice())
                .pizzas(pizzas)
                .build();
    }

    @Override
    public Order toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }

        return Order.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .date(dto.getDate())
                .price(dto.getPrice())
                // .pizzas()
                .build();
    }
}
