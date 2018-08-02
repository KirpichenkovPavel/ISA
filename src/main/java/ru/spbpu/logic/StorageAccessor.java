package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

public interface StorageAccessor extends Accessor {
    Storage getInstance() throws ApplicationException;
}
