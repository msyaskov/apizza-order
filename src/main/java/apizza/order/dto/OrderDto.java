package apizza.order.dto;

import apizza.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private UUID id;
    private UUID userId;
    private LocalDateTime date;
    private Double price;
    private OrderStatus status;
    private Collection<UUID> pizzas;

}
