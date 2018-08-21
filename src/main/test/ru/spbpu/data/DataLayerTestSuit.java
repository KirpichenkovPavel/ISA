package ru.spbpu.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataLayerTestSuit {

    AccessorRegistry registry;
    ItemAccessor itemAccessor;
    ComponentAccessor componentAccessor;
    UserAccessor userAccessor;
    StorageAccessor storageAccessor;
    OrderAccessor orderAccessor;
    PaymentAccessor paymentAccessor;

    @Before
    public void setUp() {
        String url = "jdbc:postgresql://localhost:5432/isa_test";
        registry = new AccessorRegistry();
        itemAccessor = new ItemMapper(url, registry);
        componentAccessor = new ComponentMapper(url, registry);
        userAccessor = new UserMapper(url, registry);
        storageAccessor = new StorageMapper(url, registry);
        orderAccessor = new OrderMapper(url, registry);
        paymentAccessor = new PaymentMapper(url, registry);
        registry.setUp(itemAccessor, componentAccessor, userAccessor, storageAccessor, orderAccessor, paymentAccessor);
        try {
            dropData();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    private void dropData() throws SQLException {
//        for (AccessorRegistry.RegistryKey key: AccessorRegistry.RegistryKey.values()) {
//            Accessor accessor = registry.getAccessor(key);
//            if (accessor instanceof BasicMapper) {
//                ((BasicMapper) accessor).dropData();
//            }
//        }
    }

    @After
    public void tearDown() {
        registry = null;
    }

    @Test
    public void testCreateComponent() throws ApplicationException {
        List<Component> components = new ArrayList<>();
        int listSize = 10;
        for (Integer i = 0; i < listSize; i++) {
            Component c = registry.newComponent(String.format("component %s", i.toString()));
            components.add(c);
            c.create();
        }
        List<Component> componentsInStorage = (List<Component>) componentAccessor.getAll();
        Assert.assertEquals(components.size(), componentsInStorage.size());
    }

    @Test
    public void testAddUser() throws ApplicationException {
        BaseUser manager = registry.newUser("manager", User.Role.MANAGER);
        Assert.assertTrue(manager instanceof Manager);
        Assert.assertEquals(0, manager.getId());
        manager.create();
        Assert.assertNotEquals(0, manager.getId());

        BaseUser client = registry.newUser("client", User.Role.CLIENT);
        Assert.assertTrue(client instanceof Client);
        Assert.assertEquals(0, client.getId());
        client.create();
        Assert.assertNotEquals(0, client.getId());

        BaseUser provider = registry.newUser("provider", User.Role.PROVIDER);
        Assert.assertTrue(provider instanceof Provider);
        Assert.assertEquals(0, provider.getId());
        provider.create();
        Assert.assertNotEquals(0, provider.getId());
    }

    @Test
    public void testPayment() throws ApplicationException {
        BaseUser client = registry.newUser("test client for payment", User.Role.CLIENT);
        BaseUser manager = registry.newUser("test manager for payment", User.Role.MANAGER);
        client.create();
        manager.create();
        int amount = 42;
        Payment payment = registry.newPayment(client, manager, amount);
        Assert.assertEquals(amount, payment.getAmount());
        Assert.assertEquals(0, payment.getId());
        payment.create();
        Assert.assertNotEquals(0, payment.getId());
        PaymentAccessor pa = (PaymentAccessor) registry.getAccessor(Payment.class);
        Payment fetchedPayment = (Payment) pa.getById(payment.getId());
        Assert.assertEquals(payment.getAmount(), fetchedPayment.getAmount());
        Assert.assertEquals(payment.getSourceUser().getId(), fetchedPayment.getSourceUser().getId());
        Assert.assertEquals(payment.getTargetUser().getId(), fetchedPayment.getTargetUser().getId());
    }

    @Test
    public void testM2M() throws ApplicationException {
        Client client = (Client) registry.newUser("test client for payment", User.Role.CLIENT);
        client.create();
        Component component1 = registry.newComponent("component 1");
        component1.create();
        int comp1quant = 5;
        Component component2 = registry.newComponent("component 2");
        component2.create();
        int comp2quant = 13;
        ClientOrder order = client.makeOrder();
        order.create();
        client.addItemToOrder(order, component1, comp1quant);
        client.addItemToOrder(order, component2, comp2quant);
        order.update();
        Assert.assertNotEquals(0, order.getId());
        Order fetchedOrder = (Order) orderAccessor.getById(order.getId());
        Assert.assertEquals(2, fetchedOrder.getItems().size());
    }

    @Test
    public void testStorage() throws ApplicationException {
        Storage storage = registry.getStorage();
        Component c1 = registry.newComponent("c1");
        c1.create();
        Component c2 = registry.newComponent("c2");
        c2.create();
        Component c3 = registry.newComponent("c3");
        c3.create();
        Component c4 = registry.newComponent("c4");
        c4.create();
        Item i1 = registry.newItem(c1, 5);
        i1.create();
        Item i2 = registry.newItem(c2, 3);
        i2.create();
        Item i3 = registry.newItem(c3, 2);
        i3.create();
        storage.addItem(i1);
        storage.addItem(i2);
        storage.addItem(i3);
        storage.update();
        storage.takeComponents(c1, 4);
        storage.takeComponents(c2, 1);
        storage.takeComponents(c3, 1);
        storage.takeComponents(c3, 1);
        storage.update();
        Storage fetchedStorage = (Storage) storageAccessor.getById(storage.getId());
        Assert.assertEquals(3, fetchedStorage.getItems().size());
        Assert.assertEquals(1, fetchedStorage.componentAmount(c1));
        Assert.assertEquals(2, fetchedStorage.componentAmount(c2));
        Assert.assertEquals(0, fetchedStorage.componentAmount(c3));
        Assert.assertEquals(0, fetchedStorage.componentAmount(c4));
    }
}
