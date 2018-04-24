package ru.spbpu.logic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import ru.spbpu.data.*;
import ru.spbpu.exceptions.ApplicationException;

import java.util.Optional;

public class OrderTestSuit {

    Manager manager;
    Client client;
    Storage storage;
    AccessorRegistry registry;
    ComponentAccessor ca;
    StorageAccessor sa;
    ItemAccessor ia;
    UserAccessor ua;
    PaymentAccessor pa;

    @Before
    public void setUp() throws ApplicationException{

        registry = new AccessorRegistry(new ItemRepository(), new ComponentRepository(), new UserRepository(), 
                new StorageRepository(), new OrderRepository(), new PaymentRepository());
        ca = (ComponentAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.COMPONENT);
        sa = (StorageAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.STORAGE);
        ia = (ItemAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.ITEM);
        ua = (UserAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.USER);
        pa = (PaymentAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.PAYMENT);

        storage = registry.newStorage();
        client = (Client)registry.newUser("Client", User.Role.CLIENT);
        manager = (Manager) registry.newUser("Manager", User.Role.MANAGER);
        
        storage.create();

        Component cpu = registry.newComponent("CPU");
        Component memory = registry.newComponent("Memory");
        Component hdd = registry.newComponent("HDD");
        Component gpu = registry.newComponent("GPU");

        cpu.create();
        memory.create();
        hdd.create();
        gpu.create();

        Item icpu = registry.newItem(cpu, 10);
        Item imem = registry.newItem(cpu, 5);
        Item ihdd = registry.newItem(cpu, 3);
        Item igpu = registry.newItem(cpu, 7);

        icpu.create();
        imem.create();
        ihdd.create();
        igpu.create();

        storage.addItem(icpu);
        storage.addItem(imem);
        storage.addItem(ihdd);
        storage.addItem(igpu);

        storage.update();
    }

    @After
    public void tearDown() {
        manager = null;
        client = null;
        storage = null;
        registry = null;
    }

    @Test
    public void testOrderBP() throws ApplicationException {

        Order order = client.makeOrder();
        Assert.assertEquals(order.getStatus(), Order.OrderStatus.NEW);
        Assert.assertEquals(order.getItems().size(), 0);

        Optional<Component> maybeComponent = ca.getByName("CPU");
        Assert.assertTrue(maybeComponent.isPresent());
        order.addItem(maybeComponent.get(), 5);
    }
}
