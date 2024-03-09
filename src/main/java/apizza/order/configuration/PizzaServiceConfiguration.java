package apizza.order.configuration;

import apizza.order.repository.PizzaRepository;
import apizza.order.service.pizza.DefaultPizzaService;
import apizza.order.service.pizza.PizzaApplicationEventPublisherPizzaServiceDecorator;
import apizza.order.service.pizza.PizzaService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PizzaServiceConfiguration {

    @Bean
    public PizzaService pizzaService(PizzaRepository pizzaRepository, ApplicationEventPublisher applicationEventPublisher) {
        PizzaService pizzaService = new DefaultPizzaService(pizzaRepository);
        return new PizzaApplicationEventPublisherPizzaServiceDecorator(pizzaService, applicationEventPublisher);
    }
}
