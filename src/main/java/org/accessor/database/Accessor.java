package org.accessor.database;

import org.accessor.database.connectionPool.ConnectionPool;
import org.accessor.database.connectionPool.PoolConnection;
import org.accessor.database.definition.conditions.Condition;
import org.accessor.database.definition.conditions.ConditionBuilder;
import org.accessor.database.definition.data.Field;
import org.accessor.database.definition.struckture.Attribute;
import org.accessor.database.definition.struckture.Table;
import org.accessor.database.definition.data.Row;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.*;

public class Accessor {

    public static boolean insert(Table table, Row row) {
        if (!row.isCompleteRow()) return false;
        StringBuilder sqlAttributes = new StringBuilder("(");
        StringBuilder sqlValues = new StringBuilder("(");

        for (Attribute attribute : table.getAttributes()) {
            if (attribute.isPrimaryKey()) continue;
            String attName = attribute.getName();
            sqlAttributes.append(attName).append(", ");
            sqlValues.append("'").append(Objects.requireNonNull(row.getField(attName)).getFieldData()).append("', ");
        }

        sqlAttributes.delete(sqlAttributes.length()-2, sqlAttributes.length()).append(")");
        sqlValues.delete(sqlValues.length()-2, sqlValues.length()).append(")");

        return executeSql(buildInsertSql(table.getName(), sqlAttributes.toString(), sqlValues.toString()));
    }

    public static void insert(Table table, ArrayList<Row> rows) {
        for (Row row : rows) insert(table, row);
    }

    public static boolean updateRow(Table table, Object primaryKey, Row newFields) {
        Condition condition = new ConditionBuilder().addEqualCon(
                table.getPrimaryKeyAtt(),
                primaryKey)
                .build();

        return executeSql(buildUpdateSql(table.getName(), newFields.getFieldsAsArrayList(), condition));
    }

    public static boolean updateRows(Table table, Condition condition, Row newFields) {
        return executeSql(buildUpdateSql(table.getName(), newFields.getFieldsAsArrayList(), condition));
    }

    public static boolean deleteRows(Table table, Object primaryKey) {
        Condition condition = new ConditionBuilder().addEqualCon(
                table.getPrimaryKeyAtt(),
                primaryKey)
                .build();

        return executeSql(buildDeleteSql(table.getName(), condition));
    }

    public static boolean deleteRows(Table table, Condition condition) {
        return executeSql(buildDeleteSql(table.getName(), condition));
    }

    public static Row getRow(Table table, Object primaryKey) throws SQLException {
        ResultSet set = executeSqlAndCopyResultSet(
                buildInsertSql(
                        "*",
                        table.getName(),
                        new ConditionBuilder().addEqualCon(
                                table.getPrimaryKeyAtt(),
                                primaryKey)
                                .build()));

        if (set.next()) return new Row(table, getFieldsFromResultSetLine(table, set));
        return null;
    }

    public static ArrayList<Row> getRows(Table table, Condition condition) throws SQLException {
        ResultSet set = executeSqlAndCopyResultSet(buildInsertSql("*", table.getName(), condition));
        ArrayList<Row> rows = new ArrayList<>();
        while (set.next()) rows.add(new Row(table, getFieldsFromResultSetLine(table, set)));
        return rows;
    }

    public static Field<?> getSingleRowField(Table table, String attributeName, Object primaryKey) throws SQLException {
        ResultSet set = executeSqlAndCopyResultSet(
                buildInsertSql(
                        attributeName,
                        table.getName(),
                        new ConditionBuilder().addEqualCon(
                                table.getPrimaryKeyAtt(),
                                primaryKey)
                                .build()));

        if (set.next()) return getFieldsFromResultSetLine(table, set).getFirst();
        return null;
    }

    public static ArrayList<Row> getIncompleteRows(Table table, Condition condition, ArrayList<String> targetAttributeNames) throws SQLException {
        StringBuilder selection = new StringBuilder();
        for (String attributeName : targetAttributeNames) selection.append(attributeName).append(", ");
        selection.delete(selection.length()-2, selection.length());
        ResultSet set = executeSqlAndCopyResultSet(buildInsertSql(selection.toString(), table.getName(), condition));
        ArrayList<Row> fieldsFromRow = new ArrayList<>();
        while (set.next()) fieldsFromRow.add(new Row(table, getFieldsFromResultSetLine(table, set)));
        return fieldsFromRow;
    }

    public static ArrayList<Row> getIncompleteRows(Table table, Condition condition, String... targetAttributeNames) throws SQLException {
        ArrayList<String> attributeNameList = new ArrayList<>(List.of(targetAttributeNames));
        return  getIncompleteRows(table, condition, attributeNameList);
    }

    private static ArrayList<Field<?>> getFieldsFromResultSetLine(Table table, ResultSet set) throws SQLException {
        ArrayList<Field<?>> fields = new ArrayList<>();

        for (int i = 1; set.getMetaData().getColumnCount() >= i; i++) {
            String attName = set.getMetaData().getColumnName(i);
            Attribute attribute = table.getAttribute(attName);

            Object field = switch (attribute.getType()) {
                case STRING -> set.getString(i);
                case INT -> set.getInt(i);
                case FLOAT -> set.getFloat(i);
                case BOOLEAN -> set.getBoolean(i);
                case DOUBLE -> set.getDouble(i);
                case TIME_STAMP -> set.getDate(i);
            };

            fields.add(new Field<>(attribute, field));
        }

        return fields;
    }

    private static ResultSet executeSqlAndCopyResultSet(String sql) {
        PoolConnection poolConnection = ConnectionPool.claimConnection();
        Connection connection = Objects.requireNonNull(poolConnection).getConnection();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                crs.populate(rs);
                poolConnection.unClaim();
                return crs;
            }
        } catch (SQLException e) {
            poolConnection.unClaim();
            throw new RuntimeException("Error while reading sql: " + sql, e);
        }
    }

    private static boolean executeSql(String sql) {
        PoolConnection connection = ConnectionPool.claimConnection();
        try {
            assert connection != null;
            PreparedStatement stmt = connection.getConnection().prepareStatement(sql);
            stmt.executeUpdate();
            connection.unClaim();
            return true;
        } catch (SQLException e) {
            connection.unClaim();
            return false;
        }
    }

    private static String buildInsertSql(String selectionColumn, String tableName, Condition condition) {
        return "SELECT " + selectionColumn + " FROM " + tableName + " " + condition.getAsString();
    }

    private static String buildInsertSql(String tableName, String sqlAttributes, String sqlValues) {
        return "INSERT INTO " + tableName + " " + sqlAttributes + " VALUES " + sqlValues + ";";
    }

    private static String buildUpdateSql(String tableName, ArrayList<Field<?>> fields, Condition condition) {
        StringBuilder set = new StringBuilder();
        for (Field<?> field : fields) set.append(field.getAttribute().getName()).append(" = '").append(field.getFieldData().toString()).append("',");
        set.delete(set.length()-2, set.length());
        return "UPDATE " + tableName + " SET " + set + " " + condition.getAsString() + ";";
    }

    private static String buildDeleteSql(String tableName, Condition condition) {
        return "DELETE FROM " + tableName + " " + condition.getAsString();
    }
}
