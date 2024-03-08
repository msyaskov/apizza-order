package apizza.order.service.pizza;

import apizza.order.entity.Pizza;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.show-sql=true")
class DefaultPizzaServiceTest {

    @Autowired
    private PizzaService pizzaService;

    @Test
    void test() {
        Pizza candidate = Pizza.builder()
                .name("name-" + UUID.randomUUID())
                .description("description")
                .price(199.99)
                .available(true)
                .build();

        Pizza pizza = pizzaService.addPizza(candidate);
        assertAll(() -> assertNotNull(pizza),
                () -> assertNotNull(pizza.getId()),
                () -> assertEquals(candidate.getName(), pizza.getName()),
                () -> assertEquals(candidate.getDescription(), pizza.getDescription()),
                () -> assertEquals(candidate.getPrice(), pizza.getPrice()));

        Pizza retrieved = pizzaService.getPizza(pizza.getId());
        assertAll(() -> assertNotNull(retrieved),
                () -> assertEquals(pizza.getId(), retrieved.getId()),
                () -> assertEquals(pizza.getName(), retrieved.getName()),
                () -> assertEquals(pizza.getDescription(), retrieved.getDescription()),
                () -> assertEquals(pizza.getPrice(), retrieved.getPrice()));
    }


}