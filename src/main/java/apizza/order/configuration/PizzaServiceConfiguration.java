package apizza.order.configuration;

import apizza.order.repository.PizzaRepository;
import apizza.order.service.pizza.DefaultPizzaService;
import apizza.order.service.pizza.PizzaApplicationEventPublisherPizzaServiceDecorator;
import apizza.order.service.pizza.PizzaService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PizzaServiceConfiguration {

    @Bean
    @Primary
    public PizzaService pizzaService(PizzaService pizzaService, ApplicationEventPublisher applicationEventPublisher) {
        return new PizzaApplicationEventPublisherPizzaServiceDecorator(pizzaService, applicationEventPublisher);
    }
}
