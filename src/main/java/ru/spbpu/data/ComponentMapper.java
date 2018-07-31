package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.Component;
import ru.spbpu.logic.ComponentAccessor;
import ru.spbpu.logic.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ComponentMapper extends BasicMapper implements ComponentAccessor{

    ComponentMapper(String url, AccessorRegistry registry) {
        super(url, registry);
    }

    @Override
    Entity parseResultSetEntry(ResultSet resultSet) throws ApplicationException {
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            return new Component(name, getRegistry(), id);
        } catch (SQLException e) {
            throw new ApplicationException(String.format("SQL exception: %s", e.getMessage()));
        }
    }

    @Override
    Map<String, Object> getDatabaseFields(Entity entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        Component component = (Component) entity;
        fieldMap.put("name", component.getName());
        return fieldMap;
    }

    @Override
    String getTableNameBase() {
        return "component";
    }

    @Override
    public Optional<Component> getByName(String name) {
        return null;
    }
}
