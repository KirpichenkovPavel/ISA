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
        itemAccessor = new ItemRepository();
        componentAccessor = new ComponentMapper(url, registry);
        userAccessor = new UserRepository();
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
}
