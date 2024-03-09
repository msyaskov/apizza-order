package apizza.order.service.pizza;

import apizza.order.entity.Pizza;
import apizza.order.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultPizzaService implements PizzaService {

    private final PizzaRepository pizzaRepository;

    @NonNull
    @Override
    public Pizza addPizza(@NonNull Pizza candidate) {
        if (candidate.getId() != null) {
            candidate.setId(null);
        }
        
        if (candidate.getAvailable() == null) {
            candidate.setAvailable(true);
        }

        return pizzaRepository.save(candidate);
    }

    @NonNull
    @Override
    public List<Pizza> getPizzas() {
        return pizzaRepository.findAll();
    }

    @NonNull
    @Override
    public List<Pizza> getPizzas(@NonNull Iterable<UUID> pizzaIds) {
        return pizzaRepository.findAllById(pizzaIds);
    }

    @NonNull
    @Override
    public Pizza getPizza(@NonNull final UUID pizzaId) {
        return pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new PizzaNotFoundException(msgPizzaByIdNotFoundException(pizzaId)));
    }

    @Override
    public void removePizza(@NonNull UUID pizzaId) {
        pizzaRepository.deleteById(pizzaId);
    }

    @NonNull
    @Override
    @Transactional
    public Pizza updatePizza(@NonNull UUID pizzaId, @NonNull Pizza candidate) {
        Pizza pizza = getPizza(pizzaId);
        if (candidate.getName() != null) {
            pizza.setName(candidate.getName());
        }
        if (candidate.getDescription() != null) {
            pizza.setDescription(candidate.getDescription());
        }
        if (candidate.getPrice() != null) {
            pizza.setPrice(candidate.getPrice());
        }
        if (candidate.getAvailable() != null) {
            pizza.setAvailable(candidate.getAvailable());
        }

        return pizza;
    }

    private String msgPizzaByIdNotFoundException(UUID pizzaId) {
        return "Pizza[id=%s] not found".formatted(pizzaId);
    }
}
