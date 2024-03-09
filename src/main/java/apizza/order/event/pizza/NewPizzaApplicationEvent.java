package apizza.order.event.pizza;

import apizza.order.entity.Pizza;

public class NewPizzaApplicationEvent extends PizzaApplicationEvent {

    public NewPizzaApplicationEvent(Object source, Pizza pizza) {
        super(source, pizza);
    }
}
