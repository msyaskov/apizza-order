package apizza.order.event.pizza;

import apizza.order.entity.Pizza;

public class UpdatePizzaApplicationEvent extends PizzaApplicationEvent {

    public UpdatePizzaApplicationEvent(Object source, Pizza pizza) {
        super(source, pizza);
    }
}
