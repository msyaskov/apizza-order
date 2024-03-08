package apizza.order.mapper;

import apizza.order.dto.PizzaDto;
import apizza.order.entity.Pizza;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PizzaMapper {

    public abstract PizzaDto toDto(Pizza pizza);

    public abstract Pizza toEntity(PizzaDto pizzaDto);

}
