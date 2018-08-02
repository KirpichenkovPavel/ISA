package ru.spbpu.data;

import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.Storage;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.StorageAccessor;

import java.util.List;

public class StorageRepository extends AbstractRepository implements StorageAccessor {

    public StorageRepository() {
        super();
    }

    private static Storage storage;

    public Storage getObject() throws ApplicationException {
        List<Storage> storageList = (List<Storage>) super.getAll();
        if (storageList.size() == 1)
            return storageList.get(0);
        else
            throw new ApplicationException();
    }

    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }

    @Override
    public Storage getInstance() throws ApplicationException {
        if (storage == null) {
            storage = new Storage(getRegistry());
        }
        return  storage;
    }
}
