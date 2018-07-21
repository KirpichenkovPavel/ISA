package ru.spbpu.data;

import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.BaseUser;
import ru.spbpu.logic.UserAccessor;
import ru.spbpu.logic.User;

import java.util.List;

public class UserRepository extends AbstractRepository implements UserAccessor {

    @Override
    public BaseUser getUser(String userName, User.Role role) {
        return null;
    }

    @Override
    public int addUser(String userName, User.Role role) {
        return 0;
    }

    @Override
    public List<BaseUser> getAllUsers(User.Role role) {
        return null;
    }

    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }
}
