package ru.spbpu.user;

public abstract class AbstractUser implements UserInterface {
    private String name;

    AbstractUser(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}