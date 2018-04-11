package ru.spbpu.user;

public class Manager extends AbstractUser implements User {

    public Manager(String name){
        super(name);
    }

    @Override
    public Role getRole() {
        return Role.MANAGER;
    }
}