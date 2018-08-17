package ru.spbpu.logic;

import ru.spbpu.exceptions.ApplicationException;

import java.util.List;

public interface OrderAccessor extends Accessor {
    List<ClientOrder> getOrdersByClient(Client client) throws ApplicationException;


    List<? extends Order> getOrdersBySourceUser(BaseUser user) throws ApplicationException;
    List<? extends Order> getOrdersByTargetUser(BaseUser user) throws ApplicationException;
}
