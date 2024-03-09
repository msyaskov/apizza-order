package apizza.order.itegrationtests;

import apizza.order.dto.PizzaDto;
import apizza.order.dto.UUIDListDto;
import apizza.order.entity.Pizza;
import apizza.order.security.WithMockJWTAdmin;
import apizza.order.security.WithMockJWTUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;

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
public class PizzaControllerIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private KafkaTemplate<String, PizzaDto> pizzaKafkaTemplate;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private static Stream<Arguments> testGetPizzaMethodSource() {
        return Stream.of(Arguments.of(pizza()));
    }

    @WithMockJWTUser
    @ParameterizedTest
    @MethodSource("testGetPizzaMethodSource")
    void testGetPizza(final Pizza pizza) throws Exception {
        transactionTemplate.execute(s -> entityManager.persist(pizza));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/pizzas/{pizzaId}", pizza.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PizzaDto body = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), PizzaDto.class);
        assertPizzaEqualsPizzaDto(pizza, body);
    }

    @Test
    @WithMockJWTUser
    void testGetPizza_whenPizzaNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/pizzas/{pizzaId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPizza_whenUnauthenticated() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/pizzas/{pizzaId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private static Stream<Arguments> testGetPizzasMethodSource() {
        return Stream.of(Arguments.of(pizza(), pizza(), pizza()));
    }

    @WithMockJWTUser
    @ParameterizedTest
    @MethodSource("testGetPizzasMethodSource")
    void testGetPizzas(final Pizza p1, final Pizza p2, final Pizza p3) throws Exception {
        transactionTemplate.execute(s ->
                List.of(entityManager.persist(p1), entityManager.persist(p2), entityManager.persist(p3)));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/pizzas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<PizzaDto> pizzas = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<List<PizzaDto>>() {});
        assertEquals(3, pizzas.size());

        assertPizzaEqualsPizzaDto(p1, pizzas.get(0));
        assertPizzaEqualsPizzaDto(p2, pizzas.get(1));
        assertPizzaEqualsPizzaDto(p3, pizzas.get(2));
    }

    @WithMockJWTUser
    @ParameterizedTest
    @MethodSource("testGetPizzasMethodSource")
    void testGetPizzas_whenSpecifiedIds(final Pizza p1, final Pizza p2, final Pizza p3) throws Exception {
        transactionTemplate.execute(s ->
                List.of(entityManager.persist(p1), entityManager.persist(p2), entityManager.persist(p3)));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/pizzas")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UUIDListDto.builder()
                                .ids(List.of(p1.getId(), p2.getId()))
                                .build())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<PizzaDto> pizzas = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(),
                new TypeReference<List<PizzaDto>>() {});
        assertEquals(2, pizzas.size());

        for (PizzaDto pizza : pizzas) {
            if (pizza.getId().equals(p1.getId())) {
                assertPizzaEqualsPizzaDto(p1, pizza);
            } else if (pizza.getId().equals(p2.getId())) {
                assertPizzaEqualsPizzaDto(p2, pizza);
            } else {
                throw new AssertionError("Unknown pizza: " + pizza);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("testGetPizzasMethodSource")
    void testGetPizzas_whenUnauthenticated(final Pizza p1, final Pizza p2, final Pizza p3) throws Exception {
        transactionTemplate.execute(s ->
                List.of(entityManager.persist(p1), entityManager.persist(p2), entityManager.persist(p3)));

        mvc.perform(MockMvcRequestBuilders
                        .get("/pizzas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private static Stream<Arguments> testPostPizzaMethodSource() {
        return Stream.of(Arguments.of(pizzaDto()));
    }

    @WithMockJWTAdmin
    @ParameterizedTest
    @MethodSource("testPostPizzaMethodSource")
    void testPostPizza(final PizzaDto candidate) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/pizzas")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(candidate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PizzaDto addedPizzaDto = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), PizzaDto.class);

        assertNotNull(addedPizzaDto.getId());
        candidate.setId(addedPizzaDto.getId());
        assertPizzaDtoEqualsPizzaDto(candidate, addedPizzaDto);

        Pizza fromDB = transactionTemplate.execute(s -> entityManager.find(Pizza.class, addedPizzaDto.getId()));
        assertPizzaEqualsPizzaDto(fromDB, addedPizzaDto);

        verify(pizzaKafkaTemplate).send(anyString(), anyString(), any(PizzaDto.class));
    }

    private static Stream<Arguments> testPostPizzaWhenInvalidCandidateMethodSource() {
        return Stream.of(Arguments.of(pizzaDto(b -> b.id(UUID.randomUUID()))), // not null id
                Arguments.of(pizzaDto(b -> b.name(null))),
                Arguments.of(pizzaDto(b -> b.name(""))),
                Arguments.of(pizzaDto(b -> b.description(null))),
                Arguments.of(pizzaDto(b -> b.description(""))),
                Arguments.of(pizzaDto(b -> b.price(null))),
                Arguments.of(pizzaDto(b -> b.price(-249.90))));
    }

    @WithMockJWTAdmin
    @ParameterizedTest
    @MethodSource("testPostPizzaWhenInvalidCandidateMethodSource")
    void testPostPizza_whenInvalidCandidate(final PizzaDto candidate) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/pizzas")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(candidate)))
                .andExpect(status().isBadRequest());

        verify(pizzaKafkaTemplate, never()).send(anyString(), anyString(), any(PizzaDto.class));
    }

    @WithMockJWTUser
    @ParameterizedTest
    @MethodSource("testPostPizzaMethodSource")
    void testPostPizza_whenPrincipalIsNotAdmin(final PizzaDto candidate) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/pizzas")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(candidate)))
                .andExpect(status().isForbidden());
    }

    private static Stream<Arguments> testPatchPizzaMethodSource() {
        return Stream.of(Arguments.of(pizza(), pizzaDto(b -> b.description(null))));
    }

    @WithMockJWTAdmin
    @ParameterizedTest
    @MethodSource("testPatchPizzaMethodSource")
    void testPatchPizza(final Pizza pizza, final PizzaDto patch) throws Exception {
        transactionTemplate.execute(s -> entityManager.persist(pizza));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/pizzas/{pizzaId}", pizza.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PizzaDto body = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), PizzaDto.class);
        assertAll(() -> { if (patch.getName() != null) assertEquals(patch.getName(), body.getName()); },
                () -> { if (patch.getDescription() != null) assertEquals(patch.getDescription(), body.getDescription()); },
                () -> { if (patch.getPrice() != null) assertEquals(patch.getPrice(), body.getPrice()); },
                () -> { if (patch.getAvailable() != null) assertEquals(patch.getAvailable(), body.getAvailable()); });

        verify(pizzaKafkaTemplate).send(anyString(), anyString(), any(PizzaDto.class));
    }

    private static Stream<Arguments> testPatchPizzaWhenInvalidCandidateMethodSource() {
        return Stream.of(Arguments.of(pizzaDto(b -> b.id(UUID.randomUUID()))), // not null id
                Arguments.of(pizzaDto(b -> b.name(""))),
                Arguments.of(pizzaDto(b -> b.description(""))),
                Arguments.of(pizzaDto(b -> b.price(-249.90))));
    }

    @WithMockJWTAdmin
    @ParameterizedTest
    @MethodSource("testPatchPizzaWhenInvalidCandidateMethodSource")
    void testPatchPizza_whenInvalidCandidate(final PizzaDto patch) throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch("/pizzas/{pizzaId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patch)))
                .andExpect(status().isBadRequest());

        verify(pizzaKafkaTemplate, never()).send(anyString(), anyString(), any(PizzaDto.class));
    }

    @WithMockJWTUser
    @ParameterizedTest
    @MethodSource("testPatchPizzaMethodSource")
    void testPatchPizza_whenPrincipalInNotAdmin(final Pizza pizza, final PizzaDto patch) throws Exception {
        transactionTemplate.execute(s -> entityManager.persist(pizza));

        mvc.perform(MockMvcRequestBuilders.patch("/pizzas/{pizzaId}", pizza.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patch)))
                .andExpect(status().isForbidden());
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
}
