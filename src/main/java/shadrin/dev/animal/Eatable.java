package shadrin.dev.animal;
import shadrin.dev.config.Edible;
import shadrin.dev.field.Location;
/**
 * интерфейсдля удобства прохождения итерации по списку(вспомогательный для методов)
 */
public interface Eatable {
    Edible getEdible();// возвращает enum-тип еды
    double getWeight();// сколько килограммов даёт
    void removeFrom(Location location);//удаляет съеденную добычу из локации
}
