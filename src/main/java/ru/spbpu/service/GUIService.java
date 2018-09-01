package ru.spbpu.service;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;
import ru.spbpu.data.*;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;
import ru.spbpu.util.Util.RunMode;
import ru.spbpu.logic.User.Role;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static ru.spbpu.logic.Order.OrderStatus.DONE;
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

    public void setUp(DataLayer dataLayer, RunMode runMode) {
        registry = new AccessorRegistry();
        switch (dataLayer) {
            case DB:
                StringBuilder urlBuilder = new StringBuilder("jdbc:postgresql://localhost:5432/isa");
                if (runMode == RunMode.DEBUG) {
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
                if (!(activeUser instanceof Manager)) {
                    return null;
                } else {
                    return activeUser;
                }
            case PROVIDER:
                if (!(activeUser instanceof Provider)) {
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

    public List<Triplet<Integer, String, String>> getActiveClientOrdersList() {
        Client client = getActiveClient();
        if (client != null) {
            return client
                    .getOrders()
                    .stream()
                    .filter(clientOrder -> !clientOrder.getStatus().equals(NEW))
                    .map(clientOrder -> {
                        String items = clientOrder.getItems().stream()
                                .map(item ->
                                        String.format("%s(%s)", item.getComponent().getName(), item.getAmount()))
                                .collect(Collectors.joining(", "));
                                return Triplet.with(clientOrder.getId(), items, clientOrder.getStatus().toString());
                            }
                    )
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
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(Component.class);
        Optional<Component> component = componentAccessor.getByName(name);
        return component.orElse(null);
    }

    private Component getComponentById(Integer id) throws ApplicationException {
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(Component.class);
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
        ComponentAccessor componentAccessor = (ComponentAccessor) registry.getAccessor(Component.class);
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

    public List<Quartet<Integer, String, String, String>> getAllClientOrders() {
        List<Quartet<Integer, String, String, String>> results = new ArrayList<>();
        try {
            OrderAccessor orderAccessor = (OrderAccessor) registry.getAccessor(Order.class);
            for (ClientOrder order: orderAccessor.getAllClientOrders()) {
                switch (order.getStatus()) {
                    case SUBMITTED:
                    case PAID:
                        List<Item> items = order.getItems();
                        String itemsStr = items
                                .stream()
                                .map(item ->
                                        String.format("%s(%s)", item.getComponent().getName(), item.getAmount()))
                                .collect(Collectors.joining(", "));
                        results.add(Quartet.with(order.getId(),
                                order.getFrom().getName(),
                                itemsStr,
                                order.getStatus().name()));
                        default:
                            break;
                }
            }
        } catch (ApplicationException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    public boolean cancelClientOrder(Integer id) {
        if (id == null)
            return false;
        try {
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            ClientOrder order = (ClientOrder) accessor.getById(id);
            if (order == null)
                return false;
            getActiveManager().cancelOrder(order);
            order.update();
            return true;
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean acceptClientOrder (Integer id) {
        if (id == null)
            return false;
        try {
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            ClientOrder order = (ClientOrder) accessor.getById(id);
            if (order == null)
                return false;
            getActiveManager().acceptOrder(order);
            order.update();
            return true;
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Triplet<Integer, Integer, String>> getActiveClientPayments() {
        Client client = getActiveClient();
        List<ClientOrder> orders = client.getOrders();
        List<Triplet<Integer, Integer, String>> payments = new ArrayList<>();
        for (ClientOrder order: orders) {
            Payment payment = order.getPayment();
            if (payment != null) {
                payments.add(Triplet.with(order.getId(), payment.getAmount() , payment.getStatus().name()));
            }
        }
        return payments;
    }

    public void makePayment(Integer orderId) {
        try{
            Client client = getActiveClient();
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            ClientOrder order = (ClientOrder) accessor.getById(orderId);
            client.payForOrder(order);
            order.update();
        } catch(ApplicationException ex) {
            ex.printStackTrace();
        }
    }

    public void cancelOrderByClient(Integer orderId) {
        try{
            Client client = getActiveClient();
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            ClientOrder order = (ClientOrder) accessor.getById(orderId);
            client.cancelOrder(order);
            order.update();
        } catch(ApplicationException ex) {
            ex.printStackTrace();
        }
    }

    public boolean executeClientOrder(Integer id) {
        try {
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            ClientOrder order = (ClientOrder) accessor.getById(id);
            if (order != null) {
                getActiveManager().executeOrder(order);
                order.update();
                return true;
            }
            return false;
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Pair<Integer, String>> getCompleteClientOrders() {
        try {
            Client client = getActiveClient();
            List<Pair<Integer, String>> orders = new ArrayList<>();
            for (ClientOrder order: client.getOrders()) {
                if (order.getStatus() == DONE) {
                    String itemsStr = order.getItems()
                            .stream()
                            .map(item ->
                                    String.format("%s(%s)", item.getComponent().getName(), item.getAmount()))
                            .collect(Collectors.joining(", "));
                    orders.add(Pair.with(order.getId(), itemsStr));
                }
            }
            return orders;
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void acceptCompleteOrder(Integer orderId) {
        try {
            Client client = getActiveClient();
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            ClientOrder order = (ClientOrder) accessor.getById(orderId);
            client.closeCompleteOrder(order);
            order.update();
        } catch (ApplicationException ex) {
            ex.printStackTrace();
        }
    }

    public Optional<String> loadStorage(File inputFile) {
        try {
            StorageLoader.loadData(inputFile, registry);
            return Optional.empty();
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return Optional.ofNullable(ex.getMessage());
        }
    }

    /**
    return id, item info (name, amount, price), total cost, client, status
     */
    public Quintet<Integer, List<Triplet<String, Integer, Integer>>, Integer, String, String> getOrderDetailInfo(Integer id) {
        try {
            OrderAccessor accessor = (OrderAccessor) registry.getAccessor(Order.class);
            Order order = (Order) accessor.getById(id);
            List<Item> items = order.getItems();
            int cost = 0;
            for (Item i: items) {
                cost += i.getPrice() * i.getAmount();
            }
            return Quintet.with(
                    order.getId(),
                    items.stream()
                            .map(item -> Triplet.with(
                                    item.getComponent().getName(),
                                    item.getAmount(),
                                    item.getPrice()))
                            .collect(Collectors.toList()),
                    cost,
                    order.getFrom().getName(),
                    order.getStatus().name()
            );
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
