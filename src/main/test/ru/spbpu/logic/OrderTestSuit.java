package ru.spbpu.logic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import ru.spbpu.data.*;
import ru.spbpu.exceptions.ApplicationException;

import java.sql.SQLException;
import java.util.Optional;

public class OrderTestSuit {

    private Manager manager;
    private Client client;
    private AccessorRegistry registry;
    private ComponentAccessor ca;
    private StorageAccessor sa;
    private ItemAccessor ia;
    private UserAccessor ua;
    private PaymentAccessor pa;
    private OrderAccessor oa;

    private final int cpuAmountStart = 10;
    private final int gpuAmountStart = 7;
    private final int hddAmountStart = 3;
    private final int memAmountStart = 5;

    private final int cpuPrice = 10000;
    private final int gpuPrice = 8000;

    private void dropData() throws SQLException {
        for (AccessorRegistry.RegistryKey key: AccessorRegistry.RegistryKey.values()) {
            Accessor accessor = registry.getAccessor(key);
            if (accessor instanceof BasicMapper) {
                ((BasicMapper) accessor).dropData();
            }
        }
    }

    private Storage getStorage() throws ApplicationException {
        return registry.getStorage();
    }

    @Before
    public void setUp() throws ApplicationException{

        String url = "jdbc:postgresql://localhost:5432/isa_test";
        registry = new AccessorRegistry();
        ia = new ItemMapper(url, registry);
        ca = new ComponentMapper(url, registry);
        ua = new UserMapper(url, registry);
        sa = new StorageMapper(url, registry);
        oa = new OrderMapper(url, registry);
        pa = new PaymentMapper(url, registry);
        registry.setUp(ia, ca, ua, sa, oa, pa);
        try {
            dropData();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        client = (Client) registry.newUser("Client", User.Role.CLIENT);
        manager = (Manager) registry.newUser("Manager", User.Role.MANAGER);
        client.create();
        manager.create();

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

        Storage storage = getStorage();
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

        Assert.assertEquals(cpuPrice, getStorage().componentPrice(componentCpu));
        Assert.assertEquals(gpuPrice, getStorage().componentPrice(componentGpu));

        client.addItemToOrder(order, componentCpu, cpuInOrder);
        client.addItemToOrder(order, componentCpu, additionalCpuInOrder);
        client.addItemToOrder(order, componentGpu, gpuInOrder);

        cpuTotal = cpuInOrder + additionalCpuInOrder;

        Assert.assertEquals(cpuAmountStart, getStorage().componentAmount(componentCpu));
        Assert.assertEquals(cpuAmountStart, getStorage().componentAmount(componentCpu));
        Assert.assertEquals(gpuAmountStart, getStorage().componentAmount(componentGpu));

        client.submitOrder(order);
        Assert.assertEquals(cpuAmountStart, getStorage().componentAmount(componentCpu));
        Assert.assertEquals(gpuAmountStart, getStorage().componentAmount(componentGpu));
        Assert.assertEquals(Order.OrderStatus.SUBMITTED, order.getStatus());

        manager.acceptOrder(order);
        Assert.assertEquals(Order.OrderStatus.ACCEPTED, order.getStatus());
        Assert.assertEquals(cpuAmountStart - cpuTotal, getStorage().componentAmount(componentCpu));
        Assert.assertEquals(gpuAmountStart - gpuInOrder, getStorage().componentAmount(componentGpu));
        Assert.assertEquals(hddAmountStart, getStorage().componentAmount(componentHdd));
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

    private ClientOrder makeTestOrder() throws ApplicationException{
        ClientOrder order = client.makeOrder();
        Optional<Component> comp1 = ca.getByName("CPU");
        Optional<Component> comp2 = ca.getByName("GPU");
        client.addItemToOrder(order, comp1.get(), 5);
        client.addItemToOrder(order, comp2.get(), 3);
        return order;
    }

    @Test
    public void bpNewClientOrderCancelled() throws ApplicationException{
        ClientOrder order = makeTestOrder();
        client.cancelOrder(order);
        Assert.assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    public void bpSubmittedClientOrderCancelled() throws ApplicationException {
        ClientOrder order = makeTestOrder();
        client.submitOrder(order);
        client.cancelOrder(order);
        Assert.assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    public void bpAcceptedClientOrderCancelled() throws ApplicationException {
        ClientOrder order = makeTestOrder();
        client.submitOrder(order);
        manager.acceptOrder(order);
        client.cancelOrder(order);
        Assert.assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    public void bpPayedClientOrderCancelled() throws ApplicationException {
        ClientOrder order = makeTestOrder();
        client.submitOrder(order);
        manager.acceptOrder(order);
        client.payForOrder(order);
        client.cancelOrder(order);
        Assert.assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
        Assert.assertEquals(Payment.PaymentStatus.CANCELED, order.getPayment().getStatus());
    }

    @Test
    public void bpCompleteClientOrderCancelled() throws ApplicationException {
        ClientOrder order = makeTestOrder();
        client.submitOrder(order);
        manager.acceptOrder(order);
        client.payForOrder(order);
        manager.executeOrder(order);
        client.cancelOrder(order);
        Assert.assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
        Assert.assertEquals(Payment.PaymentStatus.CANCELED, order.getPayment().getStatus());
    }

    @Test(expected = ApplicationException.class)
    public void bpApprovedClientOrderCancelled() throws ApplicationException {
        ClientOrder order = makeTestOrder();
        client.submitOrder(order);
        manager.acceptOrder(order);
        client.payForOrder(order);
        manager.executeOrder(order);
        client.closeCompleteOrder(order);
        client.cancelOrder(order);
    }

    @Test
    public void bpSubmitCancelledOrder() throws ApplicationException {
        ClientOrder order = makeTestOrder();
        client.cancelOrder(order);
        client.submitOrder(order);
        Assert.assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
    }
}
