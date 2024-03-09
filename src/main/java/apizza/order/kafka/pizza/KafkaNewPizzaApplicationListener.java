package apizza.order.kafka.pizza;

import apizza.order.dto.PizzaDto;
import apizza.order.event.pizza.NewPizzaApplicationEvent;
import apizza.order.mapper.PizzaMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class KafkaNewPizzaApplicationListener implements ApplicationListener<NewPizzaApplicationEvent> {

    private final KafkaTemplate<String, PizzaDto> pizzaKafkaTemplate;
    private final String topic;
    private final PizzaMapper pizzaMapper;

    public KafkaNewPizzaApplicationListener(KafkaTemplate<String, PizzaDto> pizzaKafkaTemplate,
                                            @Qualifier("newPizzaEventKafkaTopic") String topic,
                                            PizzaMapper pizzaMapper) {
        this.pizzaKafkaTemplate = pizzaKafkaTemplate;
        this.topic = topic;
        this.pizzaMapper = pizzaMapper;
    }


    @Override
    public void onApplicationEvent(@NonNull NewPizzaApplicationEvent event) {
        PizzaDto pizzaDto = pizzaMapper.toDto(event.getPizza());
        pizzaKafkaTemplate.send(topic, pizzaDto.getId().toString(), pizzaDto);
    }
}
