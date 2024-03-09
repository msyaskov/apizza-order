package apizza.order.itegrationtests;

import apizza.order.dto.OrderDto;
import apizza.order.dto.PizzaDto;
import apizza.order.dto.UUIDListDto;
import apizza.order.entity.Order;
import apizza.order.entity.OrderStatus;
import apizza.order.entity.Pizza;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTests
public class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @SpyBean
    private KafkaTemplate<String, OrderDto> orderKafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanDatabase() {
        transactionTemplate.execute(s -> {
            entityManager.getEntityManager().createQuery("DELETE FROM Order").executeUpdate();
            entityManager.getEntityManager().createQuery("DELETE FROM Pizza").executeUpdate();
            return 0;
        });
    }

    @Test
    void contextLoads() {
    }

    private static Stream<Arguments> testGetOrderMethodSource() {
        return Stream.of(Arguments.of(order(), pizza()));
    }

    @ParameterizedTest
    @MethodSource("testGetOrderMethodSource")
    void testGetOrder(final Order order, final Pizza pizza) throws Exception {
        transactionTemplate.execute(s -> {
            entityManager.persist(pizza);
            order.setPizzas(new LinkedList<>(List.of(pizza)));
            return entityManager.persist(order);
        });

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/orders/{orderId}", order.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        OrderDto body = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), OrderDto.class);
        assertOrderEqualsOrderDto(order, body);
    }

    @Test
    void testGetOrder_whenOrderNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/orders/{orderId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> testGetOrdersMethodSource() {
        return Stream.of(Arguments.of(order(), order(), pizza(), pizza()));
    }

    @ParameterizedTest
    @MethodSource("testGetOrdersMethodSource")
    void testGetOrders(final Order o1, final Order o2, final Pizza p1, final Pizza p2) throws Exception {
        transactionTemplate.execute(s -> {
            entityManager.persist(p1);
            entityManager.persist(p2);

            o1.setPizzas(new LinkedList<>(List.of(p1, p2)));
            entityManager.persist(o1);

            o2.setPizzas(new LinkedList<>(List.of(p1, p2)));
            return entityManager.persist(o2);
        });

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<OrderDto> orders = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<List<OrderDto>>() {});
        assertEquals(2, orders.size());

        for (OrderDto orderDto : orders) {
            if (orderDto.getId().equals(o1.getId())) {
                assertOrderEqualsOrderDto(o1, orderDto);
            } else if (orderDto.getId().equals(o2.getId())) {
                assertOrderEqualsOrderDto(o2, orderDto);
            } else {
                throw new AssertionError("Unknown order: " + orderDto);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("testGetOrdersMethodSource")
    void testGetOrdersByUserId(final Order o1, final Order o2, final Pizza p1, final Pizza p2) throws Exception {
        transactionTemplate.execute(s -> {
            entityManager.persist(p1);
            entityManager.persist(p2);

            o1.setPizzas(new LinkedList<>(List.of(p1, p2)));
            entityManager.persist(o1);

            o2.setPizzas(new LinkedList<>(List.of(p1, p2)));
            return entityManager.persist(o2);
        });

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/orders")
                        .param("userId", o1.getUserId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<OrderDto> orders = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<List<OrderDto>>() {});

        assertEquals(1, orders.size());
        assertOrderEqualsOrderDto(o1, orders.iterator().next());
    }

    private static Stream<Arguments> testPostOrderMethodSource() {
        return Stream.of(Arguments.of(orderDto(b -> b
                .userId(null)
                .date(null)
                .price(null)
                .status(null)
                .pizzas(null) // set in testMethod
        ), pizza()));
    }

    @ParameterizedTest
    @MethodSource("testPostOrderMethodSource")
    void testPostOrder(final OrderDto candidate, final Pizza pizza) throws Exception {
        transactionTemplate.execute(s -> {
            entityManager.persistAndFlush(pizza);
            candidate.setPizzas(List.of(pizza.getId()));
            return 0;
        });

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(candidate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        OrderDto body = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), OrderDto.class);

        Order fromDB = transactionTemplate.execute(s -> entityManager.find(Order.class, body.getId()));
        assertOrderEqualsOrderDto(fromDB, body);

        verify(orderKafkaTemplate).send(anyString(), anyString(), any(OrderDto.class));
    }

    private static Stream<Arguments> testPostOrderWhenInvalidCandidateMethodSource() {
        return Stream.of(Arguments.of(orderDto(b -> b.id(UUID.randomUUID()))), // not null
                Arguments.of(orderDto(b -> b.userId(UUID.randomUUID()))), // not null
                Arguments.of(orderDto(b -> b.date(LocalDateTime.now()))), // not null
                Arguments.of(orderDto(b -> b.status(OrderStatus.PENDING))), // not null
                Arguments.of(orderDto(b -> b.pizzas(null))), // null
                Arguments.of(orderDto(b -> b.pizzas(List.of())))); // empty
    }

    @ParameterizedTest
    @MethodSource("testPostOrderWhenInvalidCandidateMethodSource")
    void testPostOrder_whenInvalidCandidate(final OrderDto candidate) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(candidate)))
                .andExpect(status().isBadRequest());

        verify(orderKafkaTemplate, never()).send(anyString(), anyString(), any(OrderDto.class));
    }

    private static Stream<Arguments> testPatchOrderMethodSource() {
        return Stream.of(Arguments.of(order(), orderDto(b -> b
                .userId(null)
                .date(null)
                .price(null)
                .status(OrderStatus.COMPLETED)
                .pizzas(null))));
    }

    @ParameterizedTest
    @MethodSource("testPatchOrderMethodSource")
    void testPatchOrder(final Order order, final OrderDto patch) throws Exception {
        transactionTemplate.execute(s -> entityManager.persist(order));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/orders/{orderId}", order.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        OrderDto body = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), OrderDto.class);
        assertEquals(patch.getStatus(), body.getStatus());

        verify(orderKafkaTemplate).send(anyString(), anyString(), any(OrderDto.class));
    }

    private static Stream<Arguments> testPatchOrderWhenInvalidCandidateMethodSource() {
        return Stream.of(
                Arguments.of(orderDto(b -> b
                        .userId(UUID.randomUUID()) // not null
                        .date(null)
                        .price(null)
                        .status(OrderStatus.COMPLETED)
                        .pizzas(null))),
                Arguments.of(orderDto(b -> b
                        .userId(null)
                        .date(LocalDateTime.now()) // not null
                        .price(null)
                        .status(OrderStatus.COMPLETED)
                        .pizzas(null))),
                Arguments.of(orderDto(b -> b
                        .userId(null)
                        .date(null)
                        .price(123.132) // not null
                        .status(OrderStatus.COMPLETED)
                        .pizzas(null))),
                Arguments.of(orderDto(b -> b
                        .userId(null)
                        .date(null)
                        .price(null)
                        .status(OrderStatus.COMPLETED)
                        .pizzas(List.of()))));  // not null
    }

    @ParameterizedTest
    @MethodSource("testPatchOrderWhenInvalidCandidateMethodSource")
    void testPatchOrder_whenInvalidCandidate(final OrderDto patch) throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch("/orders/{orderId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patch)))
                .andExpect(status().isBadRequest());

        verify(orderKafkaTemplate, never()).send(anyString(), anyString(), any(OrderDto.class));
    }

    private static PizzaDto pizzaDto() {
        return pizzaDto(builder -> {});
    }
    private static PizzaDto pizzaDto(Consumer<PizzaDto.PizzaDtoBuilder> builderConsumer) {
        PizzaDto.PizzaDtoBuilder builder = PizzaDto.builder()
                .name("pizza-name-" + UUID.randomUUID())
                .description("pizza-description-" + UUID.randomUUID())
                .price(249.90)
                .available(true);
        builderConsumer.accept(builder);
        return builder.build();
    }

    private static Order order() {
        return order(b -> {});
    }
    private static Order order(Consumer<Order.OrderBuilder> builderConsumer) {
        Order.OrderBuilder builder = Order.builder()
                .userId(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .date(LocalDateTime.now().withNano(0))
                .price(998.90);
        builderConsumer.accept(builder);
        return builder.build();
    }

    private static OrderDto orderDto() {
        return orderDto(b -> {});
    }
    private static OrderDto orderDto(Consumer<OrderDto.OrderDtoBuilder> builderConsumer) {
        OrderDto.OrderDtoBuilder builder = OrderDto.builder()
                .userId(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .date(LocalDateTime.now().withNano(0))
                .price(998.90);
        builderConsumer.accept(builder);
        return builder.build();
    }

    private static Pizza pizza() {
        return pizza(builder -> {});
    }
    private static Pizza pizza(Consumer<Pizza.PizzaBuilder> builderConsumer) {
        Pizza.PizzaBuilder builder = Pizza.builder()
                .name("pizza-name-" + UUID.randomUUID())
                .description("pizza-description-" + UUID.randomUUID())
                .price(249.90)
                .available(true);
        builderConsumer.accept(builder);
        return builder.build();
    }

    private static void assertPizzaDtoEqualsPizzaDto(PizzaDto expected, PizzaDto actual) {
        if (expected == actual) {
            return;
        }

        assertAll(() -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getPrice(), actual.getPrice()),
                () -> assertEquals(expected.getAvailable(), actual.getAvailable()));
    }

    private static void assertPizzaEqualsPizzaDto(Pizza pizza, PizzaDto pizzaDto) {
        assertAll(() -> assertEquals(pizza.getId(), pizzaDto.getId()),
                () -> assertEquals(pizza.getName(), pizzaDto.getName()),
                () -> assertEquals(pizza.getDescription(), pizzaDto.getDescription()),
                () -> assertEquals(pizza.getPrice(), pizzaDto.getPrice()),
                () -> assertEquals(pizza.getAvailable(), pizzaDto.getAvailable()));
    }

    @Test
    void test() {

    }

    private static void assertOrderEqualsOrderDto(Order order, OrderDto orderDto) {
        assertAll(() -> assertEquals(order.getId(), orderDto.getId()),
                () -> assertEquals(order.getUserId(), orderDto.getUserId()),
                () -> assertEquals(order.getDate(), orderDto.getDate()),
                () -> assertEquals(order.getPrice(), orderDto.getPrice()),
                () -> assertEquals(order.getStatus(), orderDto.getStatus()),
                () -> assertEquals(order.getPizzas().size(), orderDto.getPizzas().size()));
    }
}
