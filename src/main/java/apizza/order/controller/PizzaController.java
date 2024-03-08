package apizza.order.controller;

import apizza.order.dto.PizzaDto;
import apizza.order.dto.UUIDListDto;
import apizza.order.entity.Pizza;
import apizza.order.mapper.PizzaMapper;
import apizza.order.service.pizza.PizzaService;
import apizza.order.validation.group.PatchCandidateGroup;
import apizza.order.validation.group.PostCandidateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PizzaController {

    private final PizzaService pizzaService;

    private final PizzaMapper pizzaMapper;

    @GetMapping(path = "/pizzas/{pizzaId}", produces = MediaType.APPLICATION_JSON_VALUE) // TODO for anybody
    public PizzaDto getPizza(@PathVariable UUID pizzaId) {
        Pizza pizza = pizzaService.getPizza(pizzaId);
        return mapPizzaToPizzaDto(pizza);
    }

    @GetMapping(path = "/pizzas", produces = MediaType.APPLICATION_JSON_VALUE) // TODO for anybody
    public Collection<PizzaDto> getPizzas(@RequestBody(required = false) UUIDListDto ids) {
        List<Pizza> pizzas = (ids == null || ids.getIds() == null) ? pizzaService.getPizzas() : pizzaService.getPizzas(ids.getIds());
        return pizzas.stream()
                .map(this::mapPizzaToPizzaDto)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/pizzas", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE) // TODO for admin
    public PizzaDto postPizza(@Validated(PostCandidateGroup.class) @RequestBody PizzaDto candidateDto) {
        Pizza candidate = mapPizzaDtoToPizza(candidateDto);
        Pizza pizza = pizzaService.addPizza(candidate);
        return mapPizzaToPizzaDto(pizza);
    }

    @PatchMapping(path =  "/pizzas/{pizzaId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE) // TODO for admin
    public PizzaDto patchPizza(@PathVariable UUID pizzaId,
                               @RequestBody @Validated(PatchCandidateGroup.class) PizzaDto candidateDto) {
        Pizza candidate = mapPizzaDtoToPizza(candidateDto);
        Pizza pizza = pizzaService.updatePizza(pizzaId, candidate);
        return mapPizzaToPizzaDto(pizza);
    }

    private PizzaDto mapPizzaToPizzaDto(Pizza pizza) {
        return pizzaMapper.toDto(pizza);
    }

    private Pizza mapPizzaDtoToPizza(PizzaDto pizzaDto) {
        return pizzaMapper.toEntity(pizzaDto);
    }

}