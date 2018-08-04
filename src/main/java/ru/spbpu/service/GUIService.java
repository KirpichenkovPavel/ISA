package ru.spbpu.service;

import ru.spbpu.data.*;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;
import ru.spbpu.util.Util.RunMode;
import ru.spbpu.logic.User.Role;


public class GUIService {

    private AccessorRegistry registry;
    private BaseUser activeUser;

    public enum DataLayer {
        DB,
        REPOSITORY
    }

    public void setUp(DataLayer dataLayer, RunMode testMode) {
        registry = new AccessorRegistry();
        switch (dataLayer) {
            case DB:
                StringBuilder urlBuilder = new StringBuilder("jdbc:postgresql://localhost:5432/isa");
                if (testMode == RunMode.DEBUG) {
                    urlBuilder.append("_test");
                }
                String url = urlBuilder.toString();
                ItemAccessor itemAccessor = new ItemMapper(url, registry);
                ComponentAccessor componentAccessor = new ComponentMapper(url, registry);
                UserAccessor userAccessor = new UserMapper(url, registry);
                StorageAccessor storageAccessor = new StorageMapper(url, registry);
                OrderAccessor orderAccessor = new OrderMapper(url, registry);
                PaymentAccessor paymentAccessor = new PaymentMapper(url, registry);
                registry.setUp(itemAccessor, componentAccessor, userAccessor, storageAccessor, orderAccessor, paymentAccessor);
                break;
            case REPOSITORY:
                break;
        }
    }

    public boolean login(String userName, String password, String roleString) {
        try {
            Role role;
            try {
                role = Role.valueOf(roleString);
            } catch (RuntimeException ex) {
                return false;
            }
            BaseUser user = registry.newUser(userName, role);
            if (user.login()) {
                activeUser = user;
                return true;
            } else {
                return false;
            }
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String activeUserName() {
        if (activeUser == null)
            return "Anonymous";
        return activeUser.getName();
    }

    public void logout() {
        activeUser = null;
    }
}
