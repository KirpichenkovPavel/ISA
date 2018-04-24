package ru.spbpu.data;

import ru.spbpu.logic.Storage;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.StorageAccessor;

import java.util.List;

public class StorageRepository extends AbstractRepository implements StorageAccessor {

    public StorageRepository() {
        super();
    }

    public Storage getObject() throws ApplicationException {
        List<Storage> storageList = (List<Storage>) super.getAll();
        if (storageList.size() == 1)
            return storageList.get(0);
        else
            throw new ApplicationException();
    }

//    private Map<Component, Item> components;
//
//    public StorageRepository() {
//        components = new HashMap<>();
//    }
//
//    public void addItem(String name, int amount, int price, ComponentRepository cRep) {
//        Component component = cRep.getByName(name);
//        if (component != null)
//            components.put(component, new Item(component, amount, price));
//    }
//
//    public Map<Component, Item> getComponents() {
//        return new HashMap<>(components);
//    }
//
//    public List<Item> getItems() {
//        List<Item> items = new ArrayList<>();
//        items.addAll(components.values());
//        return items;
//    }
//
//    public void saveStorage(Map<Component, Item> components) {
//        this.components = new HashMap<>(components);
//    }
//
//    @Override
//    public Storage getById(int id) {
//        return (Storage)super.getById(id);
//    }
//
//    @Override
//    public List<Storage> getAll() {
//        return (List<Storage>) super.getAll();
//    }
}
