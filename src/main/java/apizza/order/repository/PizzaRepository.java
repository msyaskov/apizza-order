package apizza.order.repository;

import apizza.order.entity.Pizza;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PizzaRepository extends CrudRepository<Pizza, UUID> {
}
