package apizza.order.service.pizza;

import apizza.order.entity.Pizza;

import java.util.List;
import java.util.UUID;

public interface PizzaService {

    Pizza addPizza(Pizza candidate);

    Pizza getPizza(UUID pizzaId);

    List<Pizza> getPizzas();

    List<Pizza> getPizzas(Iterable<UUID> pizzaIds);

    void removePizza(UUID pizzaId);

    Pizza updatePizza(UUID pizzaId, Pizza candidate);

}
