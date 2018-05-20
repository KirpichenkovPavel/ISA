package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

import java.util.List;

public interface Accessor {

    Entity getById(int id) throws ApplicationException;

    List<? extends Entity> getAll() throws ApplicationException;

    void saveObject(Entity object) throws ApplicationException;

    void updateObject(Entity object);

    int generateId();

    AccessorRegistry getRegistry();
}
