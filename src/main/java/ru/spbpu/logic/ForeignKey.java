package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

public class ForeignKey<EntityClass extends Entity> {
    private Entity entity;
    private int id;
    private Accessor accessor;
//    private Class<EntityClass> clazz;
//    private AccessorRegistry registry;

    public ForeignKey(EntityClass entity) {
        this.entity = entity;
        this.id = entity.getId();
    }

    public ForeignKey(int id, Accessor accessor) {
        this.id = id;
        this.accessor = accessor;
//        this.registry = registry;
//        this.clazz = clazz;
    }

    public Entity getEntity() throws ApplicationException {
        if (entity != null) {
            return entity;
        } else {
            return accessor.getById(id);
        }
    }

    public int getId() {
        return this.id;
    }
}
