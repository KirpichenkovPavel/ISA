package ru.spbpu.data;

import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.PaymentAccessor;

public class PaymentRepository extends AbstractRepository implements PaymentAccessor {
    @Override
    public AccessorRegistry getRegistry() {
        return null;
    }
}
