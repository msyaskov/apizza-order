package apizza.order.mapper;

import apizza.order.dto.OrderDto;
import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = PizzaMapper.class)
public abstract class OrderMapper {

    public abstract OrderDto toDto(Order order);

    protected Collection<UUID> mapPizzasToPizzaIds(Collection<Pizza> pizzas) {
        return pizzas.stream().map(Pizza::getId).toList();
    }
}
