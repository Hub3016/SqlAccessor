package org.accessor.database.definition.conditions;

public class Condition {

    private final String sqlCon;

    public Condition(String sqlCon) {
        this.sqlCon = sqlCon;
    }

    public String getAsString() {
        return sqlCon;
    }
}
