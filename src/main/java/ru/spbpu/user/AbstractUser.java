package ru.spbpu.user;

public abstract class AbstractUser implements User {
    private String name;

    AbstractUser(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}