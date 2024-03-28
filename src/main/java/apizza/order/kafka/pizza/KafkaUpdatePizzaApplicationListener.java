package apizza.order.kafka.pizza;

import apizza.order.dto.PizzaDto;
import apizza.order.event.pizza.NewPizzaApplicationEvent;
import apizza.order.event.pizza.UpdatePizzaApplicationEvent;
import apizza.order.mapper.PizzaMapper;
import apizza.order.util.logging.Logging;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class KafkaUpdatePizzaApplicationListener implements ApplicationListener<UpdatePizzaApplicationEvent> {

    private final KafkaTemplate<String, PizzaDto> pizzaKafkaTemplate;
    private final String topic;
    private final PizzaMapper pizzaMapper;

    public KafkaUpdatePizzaApplicationListener(KafkaTemplate<String, PizzaDto> pizzaKafkaTemplate,
                                               @Qualifier("updatePizzaEventKafkaTopic") String topic,
                                               PizzaMapper pizzaMapper) {
        this.pizzaKafkaTemplate = pizzaKafkaTemplate;
        this.topic = topic;
        this.pizzaMapper = pizzaMapper;
    }


    @Logging
    @Override
    public void onApplicationEvent(@NonNull UpdatePizzaApplicationEvent event) {
        PizzaDto pizzaDto = pizzaMapper.toDto(event.getPizza());
        pizzaKafkaTemplate.send(topic, pizzaDto.getId().toString(), pizzaDto);
    }
}
