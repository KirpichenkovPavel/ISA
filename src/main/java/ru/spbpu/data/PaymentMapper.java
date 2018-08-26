package ru.spbpu.data;

import ru.spbpu.exceptions.ApplicationException;
import ru.spbpu.logic.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PaymentMapper extends BasicMapper implements PaymentAccessor {

    public PaymentMapper(String url, AccessorRegistry registry) {
        super(url, registry);
    }

    @Override
    Entity parseResultSetEntry(ResultSet resultSet, String idColumn) throws ApplicationException {
        try {
            int id = resultSet.getInt("id");
            int amount = resultSet.getInt("amount");
            String status = resultSet.getString("status");
            int from_id = resultSet.getInt("from_id");
            int to_id = resultSet.getInt("to_id");
            UserAccessor userAccessor = (UserAccessor) getRegistry().getAccessor(User.class);
//            BaseUser from = (BaseUser) userAccessor.getById(from_id);
//            BaseUser to = (BaseUser) userAccessor.getById(to_id);
            ForeignKey<BaseUser> from = new ForeignKey<>(from_id, userAccessor);
            ForeignKey<BaseUser> to = new ForeignKey<>(to_id, userAccessor);
            return new Payment(from, to, amount, Payment.PaymentStatus.valueOf(status), id, getRegistry());
        } catch (SQLException e) {
            throw new ApplicationException(String.format("SQL exception: %s", e.getMessage()));
        }
    }

    @Override
    Map<String, Object> getDatabaseFields(Entity entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        Payment payment = (Payment) entity;
        fieldMap.put("amount", payment.getAmount());
        fieldMap.put("from_id", payment.getSourceUser().getId());
        fieldMap.put("to_id", payment.getTargetUser().getId());
        fieldMap.put("status", payment.getStatus().name());
        return fieldMap;
    }

    @Override
    String getTableNameBase() {
        return "payment";
    }
}
