package apizza.order.mapper;

import apizza.order.dto.PizzaDto;
import apizza.order.entity.Pizza;
import org.springframework.stereotype.Component;

@Component
public class PizzaMapper implements Mapper<Pizza, PizzaDto> {

    @Override
    public PizzaDto toDto(Pizza entity) {
        if (entity == null) {
            return null;
        }

        return PizzaDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .build();
    }

    @Override
    public Pizza toEntity(PizzaDto dto) {
        if (dto == null) {
            return null;
        }

        return Pizza.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
    }
}
