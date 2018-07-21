package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

public class BaseUser extends Entity implements User {
    private String name;

    public BaseUser(String name, AccessorRegistry registry) {
        super(registry);
        this.name = name;
    }

    public BaseUser(String name, AccessorRegistry registry, int id) {
        super(registry);
        this.name = name;
        this.setId(id);
    }

    public String getName() {
        return this.name;
    }

    public AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.USER;
    }

    @Override
    public Role getRole() {
        return Role.NONE;
    }

    @Override
    public void create() throws ApplicationException {
        UserAccessor accessor = (UserAccessor) getAccessor();
        int newId = accessor.addUser(getName(), getRole());
        setId(newId);
    }
}