package ru.spbpu.user;

public interface User {

    public enum Role {
        CLIENT,
        MANAGER,
        PROVIDER,
        NONE
    }

    public Role getRole();

    public String getName();
}
