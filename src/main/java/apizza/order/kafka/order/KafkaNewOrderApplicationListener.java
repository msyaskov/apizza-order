package apizza.order.kafka.order;

import apizza.order.dto.OrderDto;
import apizza.order.event.order.NewOrderApplicationEvent;
import apizza.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class KafkaNewOrderApplicationListener implements ApplicationListener<NewOrderApplicationEvent> {

    private final KafkaTemplate<String, OrderDto> orderKafkaTemplate;
    private final String topic;
    private final OrderMapper orderMapper;

    public KafkaNewOrderApplicationListener(KafkaTemplate<String, OrderDto> orderKafkaTemplate,
                                            @Qualifier("newOrderEventKafkaTopic") String topic,
                                            OrderMapper orderMapper) {
        this.orderKafkaTemplate = orderKafkaTemplate;
        this.topic = topic;
        this.orderMapper = orderMapper;
    }


    @Override
    public void onApplicationEvent(@NonNull NewOrderApplicationEvent event) {
        OrderDto orderDto = orderMapper.toDto(event.getOrder());
        orderKafkaTemplate.send(topic, orderDto.getId().toString(), orderDto);
    }
}
