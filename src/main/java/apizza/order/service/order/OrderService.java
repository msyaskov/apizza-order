package apizza.order.service.order;

import apizza.order.entity.Order;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD-операций над заказами.
 *
 * @author Максим Яськов
 */
public interface OrderService {

    /**
     * Добавляет (создает) новый заказ используя кандидата.
     *
     * Реализации должны могут изменять кандидата по своему усмотрению.
     * Кандидат должен быть приведен к виду добавленного заказа,
     * т.е. результат метода должен быть равен приведенному кандидату.
     *
     * @param candidate кандидат (прототип) заказа
     * @return добавленный заказ, всегда не null.
     * @throws apizza.order.service.InvalidCandidateException если недопустимый кандидат
     */
    @NonNull
    Order addOrder(@NonNull Order candidate);

    /**
     * Возвращает список всех заказов.
     *
     * @return список всех заказов, всегда не null
     */
    @NonNull
    List<Order> getOrders();

    /**
     * Возвращает список всех заказов указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список всех заказов указанного пользователя, всегда не null
     */
    @NonNull
    List<Order> getOrdersByUserId(@NonNull UUID userId);

    /**
     * Возвращает заказ по указанному идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return заказ, всегда не null
     * @throws OrderNotFoundException если заказ не найден
     */
    @NonNull
    Order getOrder(@NonNull UUID orderId);

    /**
     * Удаляет заказ по указанному идентификатору.
     *
     * @param orderId идентификатор заказа
     * @throws OrderNotFoundException если заказ не найден
     */
    void removeOrder(@NonNull UUID orderId);

    /**
     * зменяет (patch) заказ используя значения кандидата.
     * Реализация может сама определять значения каких свойств кандидата использовать для изменения.
     *
     * @param orderId идентификатор изменяемого заказа
     * @param candidate кандидат
     * @return измененный заказ, всегда не null
     * @throws OrderNotFoundException если заказ не найден
     * @throws apizza.order.service.InvalidCandidateException если недопустимый кандидат
     */
    @NonNull
    Order updateOrder(@NonNull UUID orderId, @NonNull Order candidate);
}
