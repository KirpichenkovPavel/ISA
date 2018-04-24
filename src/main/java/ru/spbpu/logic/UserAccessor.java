package ru.spbpu.logic;

import java.util.List;

public interface UserAccessor extends Accessor{

    public User getUser(String name, User.Role role);

    public void addUser(String name, User.Role role);

    public List<User> getAllUsers(User.Role role);
}
