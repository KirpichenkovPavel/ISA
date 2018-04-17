package ru.spbpu.assembly;

import ru.spbpu.repository.Accessor;
import ru.spbpu.repository.AbstractStorableObject;
import ru.spbpu.repository.ItemRepository;

public class Item extends AbstractStorableObject{
    private Component component;
    private int price;
    private int amount;

    public Item(Component component, int amount, int price) {
        this.amount = amount;
        this.component = component;
        this.price = price;
    }
    
    public Item(Component component, int amount){
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
    protected Accessor getAccessor() {
        return new ItemRepository();
    }
}
