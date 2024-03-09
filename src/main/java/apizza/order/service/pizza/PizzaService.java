package apizza.order.service.pizza;

import apizza.order.entity.Pizza;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD-операций над заказами.
 *
 * @author Максим Яськов
 */
public interface PizzaService {

    /**
     * Добавляет (создает) новую пиццу используя кандидата.
     *
     * Реализации должны могут изменять кандидата по своему усмотрению.
     * Кандидат должен быть приведен к виду добавленной пиццы,
     * т.е. результат метода должен быть равен приведенному кандидату.
     *
     * @param candidate кандидат (прототип) пиццы
     * @return добавленная пицца, всегда не null.
     * @throws apizza.order.service.InvalidCandidateException если недопустимый кандидат
     */
    @NonNull
    Pizza addPizza(@NotNull Pizza candidate);

    /**
     * Возвращает пиццу по указанному идентификатору.
     *
     * @param pizzaId идентификатор пиццы
     * @return пицца, всегда не null
     * @throws PizzaNotFoundException если пицца не найдена
     */
    @NonNull
    Pizza getPizza(@NonNull UUID pizzaId);

    /**
     * Возвращает список всех пицц.
     *
     * @return список всех пицц, всегда не null
     */
    @NonNull
    List<Pizza> getPizzas();

    /**
     * Возвращает список пицц по указанным идентификаторам.
     *
     * @param pizzaIds идентификаторы пицц
     * @return список пицц, всегда не null
     * @throws PizzaNotFoundException если хотя бы одна пицца не найдена
     */
    @NonNull
    List<Pizza> getPizzas(@NonNull Iterable<UUID> pizzaIds);

    /**
     * Удаляет пиццу по указанному идентификатору.
     *
     * @param pizzaId идентификатор пиццы
     * @throws PizzaNotFoundException если пицца не найдена
     */
    void removePizza(@NonNull UUID pizzaId);

    /**
     * зменяет (patch) пиццу используя значения кандидата.
     * Реализация может сама определять значения каких свойств кандидата использовать для изменения.
     *
     * @param pizzaId идентификатор изменяемой пиццы
     * @param candidate кандидат
     * @return измененная пицца, всегда не null
     * @throws PizzaNotFoundException если пицца не найдена
     * @throws apizza.order.service.InvalidCandidateException если недопустимый кандидат
     */
    @NonNull
    Pizza updatePizza(@NonNull UUID pizzaId, @NonNull Pizza candidate);

}
