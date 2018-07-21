package ru.spbpu.logic;

public class Provider extends BaseUser implements User {

    Provider(String name, AccessorRegistry registry) {
        super(name, registry);
    }

    @Override
    public Role getRole() {
        return Role.PROVIDER;
    }

}
