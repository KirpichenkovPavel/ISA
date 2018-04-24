package ru.spbpu.data;

import ru.spbpu.logic.Component;
import ru.spbpu.logic.ComponentAccessor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComponentRepository extends AbstractRepository implements ComponentAccessor {

    @Override
    public Optional<Component> getByName(String name) {
        return  (Optional<Component>) this.getAll()
                .stream()
                .filter(component -> ((Component)component).getName().equals(name)).findFirst();
    }
}
