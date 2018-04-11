package ru.spbpu.repository;

import ru.spbpu.user.User;

import java.util.List;

public interface UserAccessor {

    public User getUser(String name, User.Role role);

    public void addUser(String name, User.Role role);

    public List<User> getAllUsers(User.Role role);
}
