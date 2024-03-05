package apizza.order.mapper;

/**
 * Преобразователь entity в dto и обратно.
 * Реализации должны сохранять null (если entity равно null, то dto тоже null и наоборот).
 *
 * @param <E> тип сущности
 * @param <D> тип dto
 *
 * @author Максим Яськов
 */
public interface Mapper<E,D> {

    D toDto(E entity);

    E toEntity(D dto);

}
