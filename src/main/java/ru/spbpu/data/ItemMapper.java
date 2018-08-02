package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import ru.spbpu.logic.Item;

public class ItemMapper extends BasicMapper implements ItemAccessor {

    public ItemMapper(String url, AccessorRegistry registry) {
        super(url, registry);
    }

    @Override
    Entity parseResultSetEntry(ResultSet resultSet, String idColumn) throws ApplicationException {
        try {
            int id = resultSet.getInt(idColumn);
            int component_id = resultSet.getInt("component_id");
            int price = resultSet.getInt("price");
            int amount = resultSet.getInt("amount");
            Accessor componentAccessor = getRegistry().getAccessor(AccessorRegistry.RegistryKey.COMPONENT);
            Component component = (Component) componentAccessor.getById(component_id);
            return new Item(component, amount, price, id, getRegistry());
        } catch (SQLException e) {
            throw new ApplicationException(String.format("SQL exception: %s", e.getMessage()));
        }
    }

    @Override
    Map<String, Object> getDatabaseFields(Entity entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        Item item = (Item) entity;
        fieldMap.put("component_id", item.getComponent().getId());
        fieldMap.put("amount", item.getAmount());
        fieldMap.put("price", item.getPrice());
        return fieldMap;
    }

    @Override
    String getTableNameBase() {
        return "item";
    }
}
