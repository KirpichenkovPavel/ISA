package ru.spbpu.repository;

import ru.spbpu.user.Client;
import ru.spbpu.user.Manager;
import ru.spbpu.user.Provider;
import ru.spbpu.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository extends AbstractRepository implements UserAccessor {

    @Override
    public User getUser(String name, User.Role role) {
        return null;
    }

    @Override
    public void addUser(String name, User.Role role) {

    }

    @Override
    public List<User> getAllUsers(User.Role role) {
        return null;
    }
}
