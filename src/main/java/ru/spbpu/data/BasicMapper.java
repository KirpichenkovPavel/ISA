package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.Accessor;
import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class BasicMapper implements Accessor {

    private String url;
    private Connection connection;
    private AccessorRegistry registry;

    BasicMapper(String url, AccessorRegistry registry) {
        this.url = url;
        this.registry = registry;
    }

    private void connect() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user","isa");
        props.setProperty("password","1sa");
        props.setProperty("ssl","true");
        connection = DriverManager.getConnection(url, props);
    }

    @Override
    public AccessorRegistry getRegistry() {
        return registry;
    }

    abstract Entity parseResultSetEntry(ResultSet resultSet) throws SQLException;

    abstract Map<String, Object> getDatabaseFields(Entity entity);

    abstract String getTableName();

    @Override
    public Entity getById(int id) throws ApplicationException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String selectString = String.format("SELECT * FROM %s WHERE id = ?", getTableName());
            PreparedStatement selectStatement = connection.prepareStatement(selectString);
            selectStatement.setInt(1, id);
            ResultSet results = selectStatement.executeQuery();
            int resultCount = results.getFetchSize();
            if (resultCount != 1) {
                String errorMessage = String.format("Wrong number of records: %d", resultCount);
                throw new ApplicationException(errorMessage, ApplicationException.Type.SQL);
            }
            return parseResultSetEntry(results);
        }
        catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }

    @Override
    public List<? extends Entity> getAll() throws ApplicationException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String selectString = String.format("SELECT * FROM %s", getTableName());
            PreparedStatement selectStatement = connection.prepareStatement(selectString);
            ResultSet resultSet = selectStatement.executeQuery();
            List<Entity> results = new ArrayList<>();
            while (resultSet.next()) {
                Entity nextResult = parseResultSetEntry(resultSet);
                results.add(nextResult);
            }
            return results;
        }
        catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }

    @Override
    public void saveObject(Entity object) throws ApplicationException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            Map<String, Object> preparedObject = getDatabaseFields(object);
            StringBuilder fieldsNames = new StringBuilder();
            StringBuilder valuesPlaceholder = new StringBuilder();
            List<Object> fieldValues = new ArrayList<>();
            boolean first = true;
            for (String key: preparedObject.keySet()) {
                fieldValues.add(preparedObject.get(key));
                if (first){
                    fieldsNames.append(String.format("%s", key));
                    valuesPlaceholder.append("?");
                } else {
                    fieldsNames.append(String.format(",%s", key));
                    valuesPlaceholder.append(",?");
                }
            }
            String insertString = String.format("INSERT INTO %s (%s) VALUES (%s)",
                    getTableName(), fieldsNames.toString(), valuesPlaceholder.toString());
            PreparedStatement insertStatement = connection.prepareStatement(insertString);
            for (int i = 0; i < fieldValues.size(); i++) {
                insertStatement.setObject(i+1, fieldValues.get(i));
            }
            insertStatement.execute();
            connection.close();
        }
        catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }

    @Override
    public void updateObject(Entity object) {

    }

    @Override
    public int generateId() {
        return 0;
    }
}
