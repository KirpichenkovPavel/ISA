package ru.spbpu.user;

public class Client extends AbstractUser implements User {

    public Client(String name) {
        super(name);
    }

    @Override
    public Role getRole() {
        return Role.CLIENT;
    }
}
