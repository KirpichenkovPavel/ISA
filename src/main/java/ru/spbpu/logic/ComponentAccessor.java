package ru.spbpu.logic;

import java.util.Optional;

public interface ComponentAccessor extends Accessor {
    Optional<Component> getByName(String name);
}
