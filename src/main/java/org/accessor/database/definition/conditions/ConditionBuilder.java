package org.accessor.database.definition.conditions;

import org.accessor.database.definition.struckture.Attribute;

public class ConditionBuilder {

    private String sql = "";

    public ConditionBuilder addEqualCon(Attribute column, Object object, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        String condition = object.toString();
        if (object.getClass() == String.class) condition = "'" + condition + "'";
        addToSql(combineOperator, useNotOperator , column.getName(), "=", condition);
        return this;
    }

    public ConditionBuilder addEqualCon(Attribute column, Object object) {
        addEqualCon(column, object, false, COMBINE_OPERATOR.NONE);
        return this;
    }

    public ConditionBuilder addHigherCon(Attribute column, Object value, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        addToSql(combineOperator, useNotOperator , column.getName(), ">", value);
        return this;
    }

    public ConditionBuilder addHigherCon(Attribute column, Object value) {
        addToSql(COMBINE_OPERATOR.NONE, false, column.getName(), ">", value);
        return this;
    }

    public ConditionBuilder addHigherOrEqualCon(Attribute column, Object value, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        addToSql(combineOperator, useNotOperator , column.getName(), ">=", value);
        return this;
    }

    public ConditionBuilder addHigherOrEqualCon(Attribute column, Object value) {
        addToSql(COMBINE_OPERATOR.NONE, false, column.getName(), ">=", value);
        return this;
    }

    public ConditionBuilder addLowerCon(Attribute column, Object value, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        addToSql(combineOperator, useNotOperator , column.getName(), "<", value);
        return this;
    }

    public ConditionBuilder addLowerCon(Attribute column, Object value) {
        addToSql(COMBINE_OPERATOR.NONE, false, column.getName(), "<", value);
        return this;
    }

    public ConditionBuilder addLowerOrEqualCon(Attribute column, Object value, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        addToSql(combineOperator, useNotOperator , column.getName(), "<=", value);
        return this;
    }

    public ConditionBuilder addLowerOrEqualCon(Attribute column, Object value) {
        addToSql(COMBINE_OPERATOR.NONE, false, column.getName(), "<=", value);
        return this;
    }

    public ConditionBuilder addInCon(Attribute column, boolean useNotOperator, COMBINE_OPERATOR combineOperator, String... params) {
        String condition = "(";
        StringBuilder conditionBuilder = new StringBuilder(condition);
        for (String param : params) conditionBuilder.append("'").append(param).append("',");
        conditionBuilder.deleteCharAt(conditionBuilder.length()-1).append(")");
        addToSql(combineOperator, useNotOperator, column.getName(), "IN", conditionBuilder.toString());
        return this;
    }

    public ConditionBuilder addInCon(Attribute column, String... params) {
        addInCon(column, false, COMBINE_OPERATOR.NONE, params);
        return this;
    }

    public ConditionBuilder addLikeCon(Attribute column, String condition, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        String likeCondition = "'" + condition + "%'";
        addToSql(combineOperator,useNotOperator , column.getName(), "LIKE", likeCondition);
        return this;
    }

    public ConditionBuilder addLikeCon(Attribute column, String condition) {
        addLikeCon(column, condition, false, COMBINE_OPERATOR.NONE);
        return this;
    }

    public ConditionBuilder addBetweenCon(Attribute column, int value1, int value2, boolean useNotOperator, COMBINE_OPERATOR combineOperator) {
        addToSql(combineOperator,useNotOperator , column.getName(), "BETWEEN", value1, "AND", value2);
        return this;
    }

    public ConditionBuilder addBetweenCon(Attribute column, int value1, int value2) {
        addBetweenCon(column, value1, value2, false, COMBINE_OPERATOR.NONE);
        return this;
    }

    public Condition build() {
        return new Condition("WHERE" + sql + ";");
    }

    private void addToSql(COMBINE_OPERATOR combineOperator, boolean useNotOperator, Object... objects) {
        String condition = "";
        StringBuilder conditionBuilder = new StringBuilder(condition);
        if (!sql.isEmpty()) conditionBuilder.append(" ").append(combineOperator.getString());
        if (useNotOperator) conditionBuilder.append(" NOT");
        for (Object object : objects) conditionBuilder.append(" ").append(object.toString());
        condition = conditionBuilder.toString();
        sql += condition;
    }

    public enum COMBINE_OPERATOR {
        NONE(""),
        AND("AND"),
        OR("OR");

        private final String string;

        COMBINE_OPERATOR(String s) {
            this.string = s;
        }

        public String getString() {
            return string;
        }
    }

}
