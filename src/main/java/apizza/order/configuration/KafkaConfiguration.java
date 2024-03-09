package apizza.order.configuration;

import apizza.order.dto.OrderDto;
import apizza.order.dto.PizzaDto;
import apizza.order.event.order.NewOrderApplicationEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfiguration.KafkaConfigurationProperties.class)
public class KafkaConfiguration {

    private final KafkaConfigurationProperties properties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", properties.getBootstrapServers())
        ));
    }

    @Bean
    public ProducerFactory<String, OrderDto> orderProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, OrderDto> orderKafkaTemplate() {
        return new KafkaTemplate<>(orderProducerFactory());
    }

    @Bean
    public ProducerFactory<String, PizzaDto> pizzaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));
    }


    @Bean
    public KafkaTemplate<String, PizzaDto> pizzaKafkaTemplate() {
        return new KafkaTemplate<>(pizzaProducerFactory());
    }

    @Bean
    public String newOrderEventKafkaTopic() {
        return "new-order-events";
    }

    @Bean
    public String updateOrderEventKafkaTopic() {
        return "update-order-events";
    }

    @Bean
    public String newPizzaEventKafkaTopic() {
        return "new-pizza-events";
    }

    @Bean
    public String updatePizzaEventKafkaTopic() {
        return "update-pizza-events";
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "spring.kafka")
    public static class KafkaConfigurationProperties {

        private List<String> bootstrapServers = List.of();

    }

}
