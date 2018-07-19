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
        props.setProperty("password","isa");
        props.setProperty("ssl","true");
        connection = DriverManager.getConnection(url, props);
    }

    @Override
    public AccessorRegistry getRegistry() {
        return registry;
    }

    abstract Entity parseResultSetEntry(ResultSet resultSet) throws ApplicationException;

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

    private String makeInsertStatement(Entity object, List<Object> fieldValuesContainer) {
        Map<String, Object> preparedObject = getDatabaseFields(object);
        StringBuilder fieldsNames = new StringBuilder();
        StringBuilder valuesPlaceholder = new StringBuilder();
        boolean first = true;
        for (String key: preparedObject.keySet()) {
            fieldValuesContainer.add(preparedObject.get(key));
            if (first){
                fieldsNames.append(String.format("%s", key));
                valuesPlaceholder.append("?");
                first = false;
            } else {
                fieldsNames.append(String.format(",%s", key));
                valuesPlaceholder.append(",?");
            }
        }
        return String.format("INSERT INTO %s (%s) VALUES (%s) returning (id)",
                getTableName(), fieldsNames.toString(), valuesPlaceholder.toString());
    }

    @Override
    public int saveObject(Entity object) throws ApplicationException {
        List<Object> values = new ArrayList<>();
        String statement = makeInsertStatement(object, values);
        return executeCreateOrUpdateStatement(statement, values);
    }

    private String makeUpdateStatement(Entity object, List<Object> fieldValuesContainer) {
        Map<String, Object> preparedObject = getDatabaseFields(object);
        boolean first = true;
        StringBuilder fieldBuilder = new StringBuilder();
        for (String key: preparedObject.keySet()) {
            fieldValuesContainer.add(preparedObject.get(key));
            if (first){
                first = false;
                fieldBuilder.append(String.format("%s = ?", key));
            } else {
                fieldBuilder.append(String.format(",%s = ?", key));
            }
        }
        return String.format("UPDATE %s SET %s WHERE id = %s RETURNING id",
                getTableName(), fieldBuilder.toString(), object.getId());
    }

    @Override
    public int updateObject(Entity object) throws ApplicationException{
        try {
            if (object.getId() < 1) {
                throw new ApplicationException("Object has no id, so can not be updated", ApplicationException.Type.SQL);
            }
        } catch (NullPointerException e) {
            throw new ApplicationException("Object has no id, so can not be updated", ApplicationException.Type.SQL);
        }
        List<Object> values = new ArrayList<>();
        String statement = makeUpdateStatement(object, values);
        return executeCreateOrUpdateStatement(statement, values);
    }

    private int executeCreateOrUpdateStatement(String statement, List<Object> fieldValues) throws ApplicationException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            PreparedStatement insertStatement = connection.prepareStatement(statement);
            for (int i = 0; i < fieldValues.size(); i++) {
                insertStatement.setObject(i+1, fieldValues.get(i));
            }
            insertStatement.execute();
            ResultSet returning = insertStatement.getResultSet();
            if (returning.next()) {
                int id = returning.getInt("id");
                connection.close();
                return id;
            }
            else {
                throw new ApplicationException("SQL exception: id was not returned on creation or update",
                        ApplicationException.Type.SQL);
            }
        }
        catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }
}
