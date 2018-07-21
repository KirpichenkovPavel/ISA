package ru.spbpu.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;

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
        String url = "jdbc:postgresql://localhost:5432/isa";
        registry = new AccessorRegistry();
        itemAccessor = new ItemMapper(url, registry);
        componentAccessor = new ComponentMapper(url, registry);
        userAccessor = new UserMapper(url, registry);
        storageAccessor = new StorageRepository();
        orderAccessor = new OrderRepository();
        paymentAccessor = new PaymentRepository();
        registry.setUp(itemAccessor, componentAccessor, userAccessor, storageAccessor, orderAccessor, paymentAccessor);
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
        BaseUser manager = registry.newUser("test manager", User.Role.MANAGER);
        Assert.assertTrue(manager instanceof Manager);
        Assert.assertEquals(0, manager.getId());
        manager.create();
        Assert.assertNotEquals(0, manager.getId());
    }

    @Test
    public void debug() throws ApplicationException {
        Component c = registry.newComponent("debug component");
        c.create();
        c.setName("name changed");
        c.update();
        Item i = registry.newItem(c, 5);
        i.create();
        i.setPrice(100);
        i.update();
        System.out.println("End");
    }
}
