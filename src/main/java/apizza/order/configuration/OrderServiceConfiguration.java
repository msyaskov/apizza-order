package apizza.order.configuration;

import apizza.order.service.order.OrderApplicationEventPublisherOrderServiceDecorator;
import apizza.order.service.order.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OrderServiceConfiguration {

    @Bean
    @Primary
    public OrderService orderService(OrderService orderService, ApplicationEventPublisher applicationEventPublisher) {
        return new OrderApplicationEventPublisherOrderServiceDecorator(orderService, applicationEventPublisher);
    }

}
