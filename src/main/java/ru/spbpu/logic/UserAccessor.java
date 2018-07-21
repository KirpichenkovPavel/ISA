package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

import java.util.List;

public interface UserAccessor extends Accessor{

    public BaseUser getUser(String userName, User.Role role) throws ApplicationException;

    public int addUser(String userName, User.Role role) throws ApplicationException;

    public List<BaseUser> getAllUsers(User.Role role) throws ApplicationException;
}
