package shadrin.dev.animal;
import shadrin.dev.config.Edible;
/**
 * маркерный интерфейс, для удобства прохождения итерации по списку(вспомогательный для методов)
 */
public interface Eatable {
    Edible getEdible();// возвращает enum-тип еды
    double getWeight();// сколько килограммов даёт
    void removeFrom(Location location);
}
