package ru.spbpu.data;

import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.UserAccessor;
import ru.spbpu.logic.User;

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

    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }
}
