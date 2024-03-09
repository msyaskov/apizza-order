package apizza.order.service.pizza;

import apizza.order.entity.Pizza;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractPizzaServiceDecorator implements PizzaService {

    private final PizzaService delegate;

    @NonNull
    @Override
    public Pizza addPizza(@NonNull Pizza candidate) {
        return delegate.addPizza(candidate);
    }

    @NonNull
    @Override
    public Pizza getPizza(@NonNull UUID pizzaId) {
        return delegate.getPizza(pizzaId);
    }

    @NonNull
    @Override
    public List<Pizza> getPizzas() {
        return delegate.getPizzas();
    }

    @NonNull
    @Override
    public List<Pizza> getPizzas(@NonNull Iterable<UUID> pizzaIds) {
        return delegate.getPizzas(pizzaIds);
    }

    @Override
    public void removePizza(@NonNull UUID pizzaId) {
        delegate.removePizza(pizzaId);
    }

    @NonNull
    @Override
    public Pizza updatePizza(@NonNull UUID pizzaId, @NonNull Pizza candidate) {
        return delegate.updatePizza(pizzaId, candidate);
    }
}
