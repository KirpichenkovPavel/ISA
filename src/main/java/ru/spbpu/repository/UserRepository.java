package ru.spbpu.repository;

import ru.spbpu.user.Client;
import ru.spbpu.user.Manager;
import ru.spbpu.user.Provider;
import ru.spbpu.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements UserAccessor {

    private List<User> allUsers;

    public UserRepository() {
        allUsers = new ArrayList<>();
    }

    @Override
    public User getUser(String name, User.Role role) {
        for (User user: allUsers) {
            if (user.getName().equals(name) && user.getRole().equals(role))
                return user;
        }
        return null;
    }

    @Override
    public void addUser(String name, User.Role role) {
        User newUser;
        switch (role) {
            case CLIENT:
                newUser = new Client(name);
                break;
            case MANAGER:
                newUser = new Manager(name);
                break;
            case PROVIDER:
                newUser = new Provider(name);
                break;
            default:
                return;
        }
        allUsers.add(newUser);
    }

    @Override
    public List<User> getAllUsers(User.Role role) {
        return new ArrayList<>(allUsers);
    }
}
