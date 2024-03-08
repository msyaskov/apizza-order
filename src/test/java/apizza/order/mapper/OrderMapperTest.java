package apizza.order.mapper;

import apizza.order.dto.OrderDto;
import apizza.order.dto.PizzaDto;
import apizza.order.entity.Order;
import apizza.order.entity.Pizza;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testToDto() {
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .date(LocalDateTime.now())
                .price(123.132)
                .pizzas(List.of(new Pizza(UUID.randomUUID(), "pizzaName", "pizzaDescription", 199.99, true)))
                .build();

        final OrderDto dto = orderMapper.toDto(order);
        assertAll(() -> assertNotNull(dto),
                () -> assertEquals(order.getId(), dto.getId()),
                () -> assertEquals(order.getUserId(), dto.getUserId()),
                () -> assertEquals(order.getDate(), dto.getDate()),
                () -> assertEquals(order.getPrice(), dto.getPrice()),
                () -> {
                    List<UUID> orderPizzaIds = order.getPizzas().stream().map(Pizza::getId).toList();
                    assertEquals(orderPizzaIds, dto.getPizzas());
                });
    }
}