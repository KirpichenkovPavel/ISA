package ru.spbpu.repository;

import java.util.List;

public interface Accessor {

    AbstractStorableObject getById(int id);

    List<? extends AbstractStorableObject> getAll();

    void saveObject(AbstractStorableObject object);

    void updateObject(AbstractStorableObject object);

    int generateId();
}
