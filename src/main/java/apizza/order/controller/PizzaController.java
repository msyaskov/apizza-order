package apizza.order.controller;

import apizza.order.dto.PizzaDto;
import apizza.order.dto.UUIDListDto;
import apizza.order.entity.Pizza;
import apizza.order.mapper.PizzaMapper;
import apizza.order.service.pizza.PizzaService;
import apizza.order.util.logging.Logging;
import apizza.order.validation.group.PatchCandidateGroup;
import apizza.order.validation.group.PostCandidateGroup;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Logging
@RestController
@RequiredArgsConstructor
public class PizzaController {

    private final PizzaService pizzaService;

    private final PizzaMapper pizzaMapper;

    @PreAuthorize("authenticated")
    @Operation(summary = "Get a pizza by id")
    @GetMapping(path = "/pizzas/{pizzaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PizzaDto getPizza(@PathVariable UUID pizzaId) {
        Pizza pizza = pizzaService.getPizza(pizzaId);
        return mapPizzaToPizzaDto(pizza);
    }

    @PreAuthorize("authenticated")
    @Operation(summary = "Get all pizzas")
    @GetMapping(path = "/pizzas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<PizzaDto> getPizzas(@RequestBody(required = false) UUIDListDto ids) {
        List<Pizza> pizzas = (ids == null || ids.getIds() == null) ? pizzaService.getPizzas() : pizzaService.getPizzas(ids.getIds());
        return pizzas.stream()
                .map(this::mapPizzaToPizzaDto)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new pizzas by candidate")
    @PostMapping(path = "/pizzas", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public PizzaDto postPizza(@Validated(PostCandidateGroup.class) @RequestBody PizzaDto candidateDto) {
        Pizza candidate = mapPizzaDtoToPizza(candidateDto);
        Pizza pizza = pizzaService.addPizza(candidate);
        return mapPizzaToPizzaDto(pizza);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Change a pizza by candidate")
    @PatchMapping(path =  "/pizzas/{pizzaId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
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
