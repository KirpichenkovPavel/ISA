package ru.spbpu.logic;

import java.util.ArrayList;
import java.util.List;

public class Provider extends BaseUser implements User {

    Provider(String name, AccessorRegistry registry) {
        super(name, registry);
    }

    @Override
    public Role getRole() {
        return Role.PROVIDER;
    }

    public List<Item> getItems() {
        return new ArrayList<>();
    }

}
