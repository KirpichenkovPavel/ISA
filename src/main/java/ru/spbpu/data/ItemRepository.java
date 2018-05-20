package ru.spbpu.data;

import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.ItemAccessor;

public class ItemRepository extends AbstractRepository implements ItemAccessor {
    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }
}
