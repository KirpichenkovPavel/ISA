package ru.spbpu.logic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import ru.spbpu.data.*;
import ru.spbpu.exceptions.ApplicationException;

import java.util.Optional;

public class OrderTestSuit {

    private Manager manager;
    private Client client;
    private Storage storage;
    private AccessorRegistry registry;
    private ComponentAccessor ca;
    private StorageAccessor sa;
    private ItemAccessor ia;
    private UserAccessor ua;
    private PaymentAccessor pa;

    private final int cpuAmountStart = 10;
    private final int gpuAmountStart = 7;
    private final int hddAmountStart = 3;
    private final int memAmountStart = 5;

    private final int cpuPrice = 10000;
    private final int gpuPrice = 8000;


    @Before
    public void setUp() throws ApplicationException{

        registry = new AccessorRegistry(new ItemRepository(), new ComponentRepository(), new UserRepository(), 
                new StorageRepository(), new OrderRepository(), new PaymentRepository());
        ca = (ComponentAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.COMPONENT);
        sa = (StorageAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.STORAGE);
        ia = (ItemAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.ITEM);
        ua = (UserAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.USER);
        pa = (PaymentAccessor) registry.getAccessor(AccessorRegistry.RegistryKey.PAYMENT);

        storage = registry.getStorage();
        client = (Client) registry.newUser("Client", User.Role.CLIENT);
        manager = (Manager) registry.newUser("Manager", User.Role.MANAGER);
        
        storage.create();

        Component cpu = registry.newComponent("CPU");
        Component mem = registry.newComponent("Memory");
        Component hdd = registry.newComponent("HDD");
        Component gpu = registry.newComponent("GPU");

        cpu.create();
        mem.create();
        hdd.create();
        gpu.create();

        Item icpu = registry.newItem(cpu, cpuAmountStart);
        Item imem = registry.newItem(mem, memAmountStart);
        Item ihdd = registry.newItem(hdd, hddAmountStart);
        Item igpu = registry.newItem(gpu, gpuAmountStart);

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
    public void bpOrderCorrect() throws ApplicationException {

        final int cpuInOrder = 5;
        final int gpuInOrder = 3;
        final int additionalCpuInOrder = 2;
        int cpuTotal = 0;

        ClientOrder order = client.makeOrder();
        Assert.assertEquals(Order.OrderStatus.NEW, order.getStatus());
        Assert.assertEquals(0, order.getItems().size());

        Optional<Component> comp1 = ca.getByName("CPU");
        Assert.assertTrue(comp1.isPresent());
        Optional<Component> comp2 = ca.getByName("GPU");
        Assert.assertTrue(comp2.isPresent());
        Optional<Component> comp3 = ca.getByName("Wrong name");
        Assert.assertFalse(comp3.isPresent());
        Optional<Component> comp4 = ca.getByName("HDD");
        Assert.assertTrue(comp4.isPresent());
        Component componentCpu = comp1.get();
        Component componentGpu = comp2.get();
        Component componentHdd = comp4.get();

        manager.setPrice(componentCpu, cpuPrice);
        manager.setPrice(componentGpu, gpuPrice);

        Assert.assertEquals(cpuPrice, storage.componentPrice(componentCpu));
        Assert.assertEquals(gpuPrice, storage.componentPrice(componentGpu));

        client.addItemToOrder(order, componentCpu, cpuInOrder);
        client.addItemToOrder(order, componentCpu, additionalCpuInOrder);
        client.addItemToOrder(order, componentGpu, gpuInOrder);

        cpuTotal = cpuInOrder + additionalCpuInOrder;

        Assert.assertEquals(cpuAmountStart, storage.componentAmount(componentCpu));
        Assert.assertEquals(cpuAmountStart, storage.componentAmount(componentCpu));
        Assert.assertEquals(gpuAmountStart, storage.componentAmount(componentGpu));

        client.submitOrder(order);
        Assert.assertEquals(cpuAmountStart, storage.componentAmount(componentCpu));
        Assert.assertEquals(gpuAmountStart, storage.componentAmount(componentGpu));
        Assert.assertEquals(Order.OrderStatus.SUBMITTED, order.getStatus());

        manager.acceptOrder(order);
        Assert.assertEquals(Order.OrderStatus.ACCEPTED, order.getStatus());
        Assert.assertEquals(cpuAmountStart - cpuTotal, storage.componentAmount(componentCpu));
        Assert.assertEquals(gpuAmountStart - gpuInOrder, storage.componentAmount(componentGpu));
        Assert.assertEquals(hddAmountStart, storage.componentAmount(componentHdd));
        Assert.assertNotEquals(null, order.getPayment());
        Assert.assertEquals(cpuTotal * cpuPrice + gpuInOrder * gpuPrice, order.getPayment().getAmount());
        Assert.assertEquals(Payment.PaymentStatus.OPEN, order.getPayment().getStatus());

        client.payForOrder(order);
        Assert.assertEquals(Order.OrderStatus.PAID, order.getStatus());
        Assert.assertEquals(Payment.PaymentStatus.COMPLETE, order.getPayment().getStatus());

        manager.executeOrder(order);
        Assert.assertEquals(Order.OrderStatus.DONE, order.getStatus());

        client.closeCompleteOrder(order);
        Assert.assertEquals(Order.OrderStatus.CLOSED, order.getStatus());
    }

    @Test
    public void bpOrderCancelled() throws ApplicationException{
        ClientOrder order = client.makeOrder();
    }

}
