package ru.spbpu.data;

import ru.spbpu.logic.Accessor;
import ru.spbpu.logic.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRepository implements Accessor {

    private List<Entity> objects;

    AbstractRepository() {
        objects = new ArrayList<>();
    }

    public Entity getById(int id){
        List<Entity> results =
                objects.stream().filter(obj -> obj.getId() == id).collect(Collectors.toList());
        return objects.get(0);
    }

    public List<? extends Entity> getAll(){
        return objects;
    }

    public int saveObject(Entity newObject){
        objects.add(newObject);
        newObject.setId(objects.size());
        return newObject.getId();
    }

    public int updateObject(Entity updatedObject){
        objects = objects.stream().map(old -> {
            if (old.getId() == updatedObject.getId()) {
                return updatedObject;
            }
            return old;
        }).collect(Collectors.toList());
        return updatedObject.getId();
    }
}
