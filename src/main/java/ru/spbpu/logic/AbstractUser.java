package ru.spbpu.logic;

public abstract class AbstractUser extends Entity implements User {
    private String name;

    AbstractUser(String name, AccessorRegistry registry) {
        super(registry);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.USER;
    }
}