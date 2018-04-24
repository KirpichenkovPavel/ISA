package ru.spbpu.logic;

import java.util.List;

public interface Accessor {

    Entity getById(int id);

    List<? extends Entity> getAll();

    void saveObject(Entity object);

    void updateObject(Entity object);

    int generateId();
}
