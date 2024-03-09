package apizza.order.service.pizza;


import apizza.order.entity.Pizza;
import apizza.order.event.order.UpdateOrderApplicationEvent;
import apizza.order.event.pizza.NewPizzaApplicationEvent;
import apizza.order.event.pizza.UpdatePizzaApplicationEvent;
import apizza.order.service.order.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class PizzaApplicationEventPublisherPizzaServiceDecorator extends AbstractPizzaServiceDecorator {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PizzaApplicationEventPublisherPizzaServiceDecorator(PizzaService delegate,
                                                               ApplicationEventPublisher applicationEventPublisher) {
        super(delegate);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @NonNull
    @Override
    public Pizza addPizza(@NonNull Pizza candidate) {
        Pizza added = super.addPizza(candidate);
        applicationEventPublisher.publishEvent(new NewPizzaApplicationEvent(this, added));

        return added;
    }

    @NonNull
    @Override
    public Pizza updatePizza(@NonNull UUID pizzaId, @NonNull Pizza candidate) {
        Pizza updated = super.updatePizza(pizzaId, candidate);
        applicationEventPublisher.publishEvent(new UpdatePizzaApplicationEvent(this, updated));

        return updated;
    }
}
