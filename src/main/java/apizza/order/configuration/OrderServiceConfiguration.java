package apizza.order.configuration;

import apizza.order.repository.OrderRepository;
import apizza.order.service.order.DefaultOrderService;
import apizza.order.service.order.OrderApplicationEventPublisherOrderServiceDecorator;
import apizza.order.service.order.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderServiceConfiguration {

    @Bean
    public OrderService orderService(OrderRepository orderRepository, ApplicationEventPublisher applicationEventPublisher) {
        OrderService orderService = new DefaultOrderService(orderRepository);
        return new OrderApplicationEventPublisherOrderServiceDecorator(orderService, applicationEventPublisher);
    }

}
