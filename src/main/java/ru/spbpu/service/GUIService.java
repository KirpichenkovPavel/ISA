package ru.spbpu.service;

import ru.spbpu.data.*;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.frontend.Application;
import ru.spbpu.logic.*;
import ru.spbpu.util.Pair;
import ru.spbpu.util.Util.RunMode;
import ru.spbpu.logic.User.Role;

import java.util.*;
import java.util.stream.Collectors;

import static ru.spbpu.logic.AccessorRegistry.RegistryKey.COMPONENT;
import static ru.spbpu.logic.Order.OrderStatus.NEW;
import static ru.spbpu.logic.User.Role.CLIENT;
import static ru.spbpu.logic.User.Role.MANAGER;
import static ru.spbpu.logic.User.Role.PROVIDER;


public class GUIService {

    private AccessorRegistry registry;
    private BaseUser activeUser;
    private ClientOrder newClientOrder;

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
            user = user.login();
            if (user != null) {
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

    private BaseUser getActiveUser(Role role) {
        switch (role) {
            case CLIENT:
                if (!(activeUser instanceof Client)) {
                    return null;
                } else {
                    return activeUser;
                }
            case MANAGER:
                if (!(activeUser instanceof Client)) {
                    return null;
                } else {
                    return activeUser;
                }
            case PROVIDER:
                if (!(activeUser instanceof Client)) {
                    return null;
                } else {
                    return activeUser;
                }
            default:
                return null;
        }
    }

    public Manager getActiveManager() {
        return (Manager) getActiveUser(MANAGER);
    }

    public Client getActiveClient() {
        return (Client) getActiveUser(CLIENT);
    }

    public Provider getActiveProvider() {
        return (Provider) getActiveUser(PROVIDER);
    }

    public void addClientOrder() {
        Client client = getActiveClient();
        if (client != null)
            try {
                client.makeOrder();
            } catch (ApplicationException ex) {
                ex.printStackTrace();
            }
    }

    public void logout() {
        activeUser = null;
    }

    public void providerComponents(Provider provider) {

    }

    public void createClientOrder(Client client) {
        try {
            client.makeOrder();
        } catch (ApplicationException ex) {
            ex.printStackTrace();
        }
    }

    public List<Pair<Integer, String>> getClientOrdersList() {
        Client client = getActiveClient();
        if (client != null) {
            return client
                    .getOrders()
                    .stream()
                    .map(clientOrder ->
                            new Pair<>(clientOrder.getId(), clientOrder.getStatus().toString()))
                    .filter(integerStringPair -> !integerStringPair.getSecond().equals(NEW.name()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<StorageItem> getStorageItems() {
        try {
            Storage storage = registry.getStorage();
            List<StorageItem> items = storage.getItems().stream().map(item -> new StorageItem(
                            item.getComponent().getId(),
                            item.getComponent().getName(),
                            item.getAmount(),
                            item.getPrice()
            )).collect(Collectors.toList());
            return items;
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Component getComponentByName(String name) throws ApplicationException {
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(COMPONENT);
        Optional<Component> component = componentAccessor.getByName(name);
        return component.orElse(null);
    }

    private Component getComponentById(Integer id) throws ApplicationException {
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(COMPONENT);
        return (Component) componentAccessor.getById(id);
    }

    public void addItemToClientOrder(Integer componentId, Integer amount) {
        Client client = getActiveClient();
        if (client != null) {
            try {
                if (newClientOrder == null) {
                    newClientOrder = registry.newOrder(client);
                    newClientOrder.create();
                }
                Component component = getComponentById(componentId);
                client.addItemToOrder(newClientOrder, component, amount);
            } catch (ApplicationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<StorageItem> getNewOrderItems() {
        if (newClientOrder == null)
            return new ArrayList<>();
        return newClientOrder
                .getItems()
                .stream()
                .map(item -> new StorageItem(
                        item.getComponent().getId(),
                        item.getComponent().getName(),
                        item.getAmount(),
                        item.getPrice()))
                .collect(Collectors.toList());
    }

    public int getComponentPrice(String name) {
        try {
            Component component = getComponentByName(name);
            if (component == null)
                return -1;
            Storage storage = registry.getStorage();
            return storage.componentPrice(component);
        } catch (ApplicationException ex) {
            return -1;
        }
    }

    public Map<String, Integer> getStoragePrices() {
        Map<String, Integer> priceMap = new HashMap<>();
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(COMPONENT);
        try {
            List<Component> components = (List<Component>)componentAccessor.getAll();
            for (Component c: components) {
                try {
                    int price = registry.getStorage().componentPrice(c);
                    priceMap.put(c.getName(), price);
                } catch (ApplicationException ex) {
                    priceMap.put(c.getName(), -1);
                }
            }
            return priceMap;
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return priceMap;
        }
    }

    public void removeComponentFromNewOrder(String name) {
        try {
            newClientOrder.setItems(
                    newClientOrder
                            .getItems()
                            .stream()
                            .filter(item -> !item.getComponent().getName().equals(name))
                            .collect(Collectors.toList())
            );
            newClientOrder.update();
        } catch (ApplicationException ex) {
            ex.printStackTrace();
        }
    }

    public void discardClientOrder() {
        newClientOrder = null;
    }

    public void submitClientOrder() {
        try {
            if (newClientOrder != null) {
                getActiveClient().submitOrder(newClientOrder);
                newClientOrder = null;
            }
        } catch (ApplicationException ex) {
            ex.printStackTrace();
        }

    }

}
