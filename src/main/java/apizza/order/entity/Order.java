package apizza.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private LocalDateTime date;
    private Double price;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "order_pizzas", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "pizza_id"))
    private Collection<Pizza> pizzas;

}
