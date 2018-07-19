package ru.spbpu.logic;

public class Item extends Entity {
    private Component component;
    private int price;
    private int amount;

    public Item(Component component, int amount, int price, AccessorRegistry registry) {
        super(registry);
        this.amount = amount;
        this.component = component;
        this.price = price;
    }
    
    public Item(Component component, int amount, AccessorRegistry registry){
        super(registry);
        this.amount = amount;
        this.component = component;
        this.price = 0;
    }

    public Component getComponent() {
        return component;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    protected AccessorRegistry.RegistryKey accessorRegistryKey() {
        return AccessorRegistry.RegistryKey.ITEM;
    }
}
