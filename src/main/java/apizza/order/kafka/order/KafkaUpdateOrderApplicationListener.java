package apizza.order.kafka.order;

import apizza.order.dto.OrderDto;
import apizza.order.event.order.NewOrderApplicationEvent;
import apizza.order.event.order.OrderApplicationEvent;
import apizza.order.event.order.UpdateOrderApplicationEvent;
import apizza.order.mapper.OrderMapper;
import apizza.order.util.logging.Logging;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class KafkaUpdateOrderApplicationListener implements ApplicationListener<UpdateOrderApplicationEvent> {

    private final KafkaTemplate<String, OrderDto> orderKafkaTemplate;
    private final String topic;
    private final OrderMapper orderMapper;

    public KafkaUpdateOrderApplicationListener(KafkaTemplate<String, OrderDto> orderKafkaTemplate,
                                               @Qualifier("updateOrderEventKafkaTopic") String topic,
                                               OrderMapper orderMapper) {
        this.orderKafkaTemplate = orderKafkaTemplate;
        this.topic = topic;
        this.orderMapper = orderMapper;
    }


    @Logging
    @Override
    public void onApplicationEvent(@NonNull UpdateOrderApplicationEvent event) {
        OrderDto orderDto = orderMapper.toDto(event.getOrder());
        orderKafkaTemplate.send(topic, orderDto.getId().toString(), orderDto);
    }
}
