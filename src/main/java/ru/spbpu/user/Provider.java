package ru.spbpu.user;

public class Provider extends AbstractUser implements User {

    public Provider(String name) {
        super(name);
    }

    @Override
    public Role getRole() {
        return Role.PROVIDER;
    }

}
