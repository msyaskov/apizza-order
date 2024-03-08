package apizza.order.mapper;

import apizza.order.dto.PizzaDto;
import apizza.order.entity.Pizza;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PizzaMapperTest {

    @Autowired
    private PizzaMapper pizzaMapper;

    @Test
    public void testToDto() {
        Pizza pizza = Pizza.builder()
                .id(UUID.randomUUID())
                .name("name")
                .price(123.132)
                .description("description")
                .build();


        final PizzaDto dto = pizzaMapper.toDto(pizza);
        assertAll(() -> assertNotNull(dto),
                () -> assertEquals(pizza.getId(), dto.getId()),
                () -> assertEquals(pizza.getName(), dto.getName()),
                () -> assertEquals(pizza.getDescription(), dto.getDescription()),
                () -> assertEquals(pizza.getPrice(), dto.getPrice()));
    }

    @Test
    public void testToEntity() {
        PizzaDto dto = PizzaDto.builder()
                .id(UUID.randomUUID())
                .name("name")
                .price(123.132)
                .description("description")
                .build();


        final Pizza pizza = pizzaMapper.toEntity(dto);
        assertAll(() -> assertNotNull(pizza),
                () -> assertEquals(dto.getId(), pizza.getId()),
                () -> assertEquals(dto.getName(), pizza.getName()),
                () -> assertEquals(dto.getDescription(), pizza.getDescription()),
                () -> assertEquals(dto.getPrice(), pizza.getPrice()));
    }
}