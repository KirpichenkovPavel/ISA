package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.Accessor;
import ru.spbpu.logic.AccessorRegistry;
import ru.spbpu.logic.Entity;
import ru.spbpu.util.Pair;

import java.sql.*;
import java.util.*;

import static ru.spbpu.exceptions.ApplicationException.Type.REFLECTION;

public abstract class BasicMapper implements Accessor {

    private String url;
    private Connection connection;
    private AccessorRegistry registry;
    private final String tablePrefix = "isa";

    BasicMapper(String url, AccessorRegistry registry) {
        this.url = url;
        this.registry = registry;
    }

    protected void connect() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user","isa");
        props.setProperty("password","isa");
        props.setProperty("ssl","true");
        connection = DriverManager.getConnection(url, props);
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void dropData() throws SQLException{
        Connection c = getConnection();
        String deleteStatementSQL = String.format("TRUNCATE public.%s CASCADE", getTableName());
        PreparedStatement deleteStatement = c.prepareStatement(deleteStatementSQL);
        deleteStatement.execute();
    }

    @Override
    public AccessorRegistry getRegistry() {
        return registry;
    }

    abstract Entity parseResultSetEntry(ResultSet resultSet) throws ApplicationException;

    abstract Map<String, Object> getDatabaseFields(Entity entity);

    Map<String, Pair<List<? extends Entity>, BasicMapper>> getM2MFields(Entity entity) {
        return new HashMap<>();
    }

    abstract String getTableNameBase();

    String getTablePrefix() {
        return tablePrefix;
    }

    protected String getTableName() {
        return String.format("%s_%s", getTablePrefix(), getTableNameBase());
    }

    protected String getM2MTableName(BasicMapper listEntityMapper) {
        return String.format("%s_%s_%s", getTablePrefix(), getTableNameBase(), listEntityMapper.getTableNameBase());
    }

    @Override
    public Entity getById(int id) throws ApplicationException {
        try (Connection connection = getConnection()) {
            String selectString = String.format("SELECT * FROM %s WHERE id = ?", getTableName());
            PreparedStatement selectStatement = connection.prepareStatement(selectString);
            selectStatement.setInt(1, id);
            ResultSet results = selectStatement.executeQuery();
            if (!results.next()) {
                return null;
            }
            Entity entity = parseResultSetEntry(results);
            for (Map.Entry<String, Pair<List<? extends Entity>, BasicMapper>> entry: getM2MFields(entity).entrySet()) {
                String fieldName = entry.getKey();
                Pair<List<? extends Entity>, BasicMapper> fieldPair = entry.getValue();
                BasicMapper listEntityMapper = fieldPair.getSecond();
                List<? extends Entity> relatedEntityList = getM2MList(entity.getId(), listEntityMapper);
                try {
                    entity.setField(fieldName, relatedEntityList);
                } catch (Exception ex) {
                    throw new ApplicationException(ex.getMessage(), REFLECTION);
                }
            }
            return entity;
        }
        catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }

    @Override
    public List<? extends Entity> getAll() throws ApplicationException {
        try (Connection connection = getConnection()){
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
        int updatedId = executeCreateOrUpdateStatement(statement, values);
        object.setId(updatedId);
        this.updateM2MFields(object);
        return updatedId;
    }

    private int executeCreateOrUpdateStatement(String statement, List<Object> fieldValues) throws ApplicationException {
        try (Connection connection = getConnection()){
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

    protected List<Entity> getM2MList(int id, BasicMapper listEntityMapper) throws ApplicationException {
        try (Connection connection = getConnection()){
            List<Entity> results = new ArrayList<>();
            String firstTable = getTableName();
            String secondTable = listEntityMapper.getTableName();
            String joinTable = getM2MTableName(listEntityMapper);
            String firstBase = getTableNameBase();
            String secondBase = listEntityMapper.getTableNameBase();
            String statement = String.format((new StringBuilder())
                    .append("SELECT * ")
                    .append("FROM %s as first ")
                    .append("JOIN %s as inner on first.id = inner.%s_id ")
                    .append("JOIN %s as second on second.id = inner.%s_id ")
                    .append("WHERE first.id = ? ")
                    .toString(), firstTable, joinTable, firstBase, secondTable, secondBase);
            PreparedStatement selectListStatement = connection.prepareStatement(statement);
            selectListStatement.setInt(1, id);
            ResultSet resultSet = selectListStatement.executeQuery();
            while (resultSet.next()) {
                Entity nextResult = listEntityMapper.parseResultSetEntry(resultSet);
                results.add(nextResult);
            }
            return results;
        } catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }

    protected void updateM2MFields(Entity entity) throws ApplicationException {
        int entityId = entity.getId();
        if (getM2MFields(entity) == null)
            return;
        if (entityId == 0) {
            throw new ApplicationException("SQL exception: can't update fields of non-created object",
                    ApplicationException.Type.SQL);
        }
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            boolean operationSuccessful = true;
            for (Pair<List<? extends Entity>, BasicMapper> listEntityPair : getM2MFields(entity).values()) {
//                System.out.println("loop");
                BasicMapper listMapper = listEntityPair.getSecond();
                String joinTable = getM2MTableName(listMapper);
                String mainTableBase = getTableNameBase();
                String deleteStatement = String.format((new StringBuilder())
                        .append("DELETE FROM %s ")
                        .append("WHERE %s_id = ?")
                        .toString(), joinTable, mainTableBase);
                PreparedStatement preparedDeleteStatement = connection.prepareStatement(deleteStatement);
                preparedDeleteStatement.setInt(1, entityId);
                preparedDeleteStatement.execute();
                List<? extends Entity> actualList = listEntityPair.getFirst();
                List<String> tmpList = new ArrayList<>();
                for (int i = 0; i < actualList.size(); i++)
                    tmpList.add("(?, ?)");
                String entityColumnId = String.format("%s_id", getTableNameBase());
                String listColumnId = String.format("%s_id", listMapper.getTableNameBase());
                String placeholder = String.join(", ", tmpList);
                String insertStatement = String.format((new StringBuilder())
                        .append("INSERT INTO %s (%s, %s) ")
                        .append("VALUES %s ")
                        .append("RETURNING true")
                        .toString(), joinTable, entityColumnId, listColumnId, placeholder);
                PreparedStatement preparedInsertStatement = connection.prepareStatement(insertStatement);
                for (int i = 0; i < actualList.size(); i++) {
                    preparedInsertStatement.setInt(2 * i + 1, entity.getId());
                    preparedInsertStatement.setInt(2 * i + 2, actualList.get(i).getId());
                }
                System.out.println(preparedInsertStatement.toString());
                if (!preparedInsertStatement.execute()) {
                    operationSuccessful = false;
                }
            }
            if (operationSuccessful)
                connection.commit();
            else
                connection.rollback();
        } catch (SQLException ex) {
            throw new ApplicationException("SQL exception: " + ex.getMessage(), ApplicationException.Type.SQL);
        }
    }
}
