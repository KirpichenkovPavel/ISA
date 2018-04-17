package ru.spbpu.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRepository implements Accessor {

    private List<AbstractStorableObject> objects;

    AbstractRepository() {
        objects = new ArrayList<>();
    }

    public AbstractStorableObject getById(int id){
        List<AbstractStorableObject> results =
                objects.stream().filter(obj -> obj.getId() == id).collect(Collectors.toList());
        return objects.get(0);
    }

    public List<? extends AbstractStorableObject> getAll(){
        return objects;
    }

    public void saveObject(AbstractStorableObject newObject){
        objects.add(newObject);
    }

    public void updateObject(AbstractStorableObject updatedObject){
        objects = objects.stream().map(old -> {
            if (old.getId() == updatedObject.getId())
                return updatedObject;
            return old;
        }).collect(Collectors.toList());
    }

    public int generateId() {
        return objects.size();
    }

}
