package ru.spbpu.repository;

public abstract class AbstractStorableObject {

    private int id;
    protected Accessor accessor;

    protected AbstractStorableObject() {
        accessor = getAccessor();
        id = accessor.generateId();
    }

    protected abstract Accessor getAccessor();

    public int getId() {
        return id;
    }

    public void create() {
        getAccessor().saveObject(this);
    }

    public void update() {
        getAccessor().updateObject(this);
    }

    public AbstractStorableObject read() {
        return getAccessor().getById(id);
    }
}
